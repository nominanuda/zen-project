/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.oio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import com.nominanuda.zen.common.Check;

public class WriterOutputStream extends OutputStream {
	private Writer writer;
	private CharsetDecoder cd;

	public WriterOutputStream(Writer w, String charsetName) {
		this(w, Charset.forName(charsetName));
	}

	public WriterOutputStream(Writer w, Charset charset) {
		writer = w;
		cd = charset.newDecoder()//see Charset#encode javadoc
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);
	}

	public WriterOutputStream(Writer w) {
		this(w, "UTF-8");
	}

	@Override
	public void write(int b) throws IOException {
		write(new byte[] {(byte)b});
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0 , b.length);
	}

	@Override
	public void flush() throws IOException {
		CharBuffer cb = CharBuffer.allocate(4096);
		ByteBuffer bb = unwritten == null
			? ByteBuffer.allocate(0)
			: ByteBuffer.wrap(unwritten);
		cd.decode(bb, cb, true);
		CoderResult res = cd.flush(cb);
		Check.illegalstate.assertTrue(CoderResult.UNDERFLOW == res);
		if(cb.position() > 0) {
			writer.write(cb.array(), 0, cb.position());
		}
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		writer.close();
	}

	private byte[] unwritten = null;
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		ByteBuffer bb = null;
		if(unwritten != null) {
			byte[] buf = new byte[len + unwritten.length];
			System.arraycopy(unwritten, 0, buf, 0, unwritten.length);
			System.arraycopy(b, off, buf, unwritten.length, len);
			bb = ByteBuffer.wrap(buf);
		} else {
			bb = ByteBuffer.wrap(b, off, len);
		}
		CharBuffer cb = CharBuffer.allocate(bb.limit());
		cd.decode(bb, cb, false);
		writer.write(cb.array(), 0, cb.position());
		if(bb.remaining() > 0) {
			unwritten = new byte[bb.remaining()];
			bb.get(unwritten);
		} else {
			unwritten = null;
		}
	}
}
