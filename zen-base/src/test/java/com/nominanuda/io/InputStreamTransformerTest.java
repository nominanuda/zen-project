/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;


public class InputStreamTransformerTest {
	private byte[] eof = "la vispa teresa...".getBytes();
	private IOHelper io = new IOHelper();
	@Test
	public void testIdentityTransformer() throws IOException {
		InputStream is1 = getClass().getResourceAsStream(getClass().getSimpleName()+".class");
		byte[] barr = io.readAndClose(is1);
		InputStream is = getClass().getResourceAsStream(getClass().getSimpleName()+".class");
		IdentityTransformer t = new IdentityTransformer(is);
		byte[] barr1 = io.readAndClose(t);
		Assert.assertArrayEquals(barr, barr1);
	}
	@Test
	public void testDoublingTransformer() throws IOException {
		int size = 10000;
		InputStream is1 = new TestIs(size, true, eof);
		byte[] barr1 = io.readAndClose(is1);
		Assert.assertEquals(size * 2 + eof.length, barr1.length);
		InputStream t = new TwicingTransformer(new TestIs(size, false, new byte[0]));
		byte[] barr2 = io.readAndClose(t);
		Assert.assertEquals(size * 2 + eof.length, barr2.length);
		Assert.assertArrayEquals(barr1, barr2);
		
		try {
			io.readAndClose(
				new ExceptionalTransformer(
					new TestIs(10, false, new byte[0]))
			);
			Assert.fail();
		} catch(IOException e) {
			Throwable cause1 = e.getCause();
			Assert.assertTrue(cause1 instanceof RuntimeException);
			Throwable cause2 = cause1.getCause();
			Assert.assertTrue(cause2 instanceof IllegalStateException);
			Assert.assertEquals("boo", cause2.getMessage());
		}
	}
	private class ExceptionalTransformer extends InputStreamTransformer {
		public ExceptionalTransformer(InputStream is) {
			super(is);
		}
		@Override
		protected ByteBuffer onEof() throws IOException {
			throw new IllegalStateException("boo");
		}
		@Override
		protected void onException(Exception e) throws Exception {
			throw new RuntimeException(e);
		}
		@Override
		protected ByteBuffer onRead(ByteBuffer b) throws IOException {
			return b;
		}
	}
	private class TwicingTransformer extends InputStreamTransformer {
		public TwicingTransformer(InputStream is) {
			super(is);
		}
		@Override
		protected ByteBuffer onEof() throws IOException {
			ByteBuffer bb = ByteBuffer.wrap(eof);
			bb.position(eof.length);
			bb.limit(eof.length);
			return bb;
		}
		@Override
		protected void onException(Exception e) throws Exception {}
		@Override
		protected ByteBuffer onRead(ByteBuffer b) throws IOException {
			b.flip();
			ByteBuffer b1 = ByteBuffer.allocate(b.remaining() * 2);
			while(b.hasRemaining()) {
				byte _b = b.get();
				b1.put(_b);
				b1.put(_b);
			}
			return b1;
		}
	}
	private class TestIs extends InputStream {
		private int seq;
		private boolean reread;
		private boolean twiceTime = false;
		private byte[] eofSeq;
		private int eofPtr = 0;
		private boolean eofSent = false;
		private int len;
		int c;
		public TestIs(int size, boolean readtwice, byte[] eof) {
			len = size;
			reread = readtwice;
			eofSeq = eof;
		}
		@Override
		public int read() throws IOException {
			if(reread && twiceTime) {
				twiceTime = false;
				return c;
			}
			if(len == 0) {
				if(! eofSent && eofPtr < eofSeq.length) {
					return eofSeq[eofPtr++];
				}
				return -1;
			}
			c = seq++ % 256;
			len--;
			if(reread) {
				twiceTime = true;
			}
			return c;
		}
		
	}
}
