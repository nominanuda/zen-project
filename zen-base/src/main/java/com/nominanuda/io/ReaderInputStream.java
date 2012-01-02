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
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

public class ReaderInputStream extends InputStream {
	private Reader reader;
	private CharsetEncoder ce;
	private ByteBuffer tmp = null;

	public ReaderInputStream(Reader r, String charsetName) {
		this(r, Charset.forName(charsetName));
	}

	public ReaderInputStream(Reader r, Charset charset) {
		reader = r;
		ce = charset.newEncoder()//see Charset#encode javadoc
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);
	}

//	public ReaderInputStream(Reader r) {
//		this(r, "UTF-8");
//	}

	// InputStream Contract
	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		int num = read(b);
		if(num == 0) {
			throw new IllegalStateException("read returned 0");
		}
		return num > 0 ? b[0] : -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int size = len;
		int soFar = 0;
		if(tmp != null) {
			soFar = tmp.remaining();
			if(soFar > size) {
				tmp.get(b, off, size);
				return size;
			} else {
				tmp.get(b, off, soFar);
				tmp = null;
				off += soFar;
				len -= soFar;
				size -= soFar;
			}
		}
		char[] cbuf = new char[size];
		int numCharRead = reader.read(cbuf);
		if(numCharRead < 0) {
			if(soFar == 0) {
				return -1;
			} else {
				return soFar;
			}
		}
		CharBuffer cb = CharBuffer.wrap(cbuf);
		cb.limit(numCharRead);
		ByteBuffer bb = ce.encode(cb);
		//bb.rewind();
		int numByteRead = bb.limit();
		if(numByteRead > size) {
			bb.get(b, off, size);
			tmp = bb;
			return size + soFar;
		} else {
			bb.get(b, off, numByteRead);
			tmp = null;
			return numByteRead + soFar;
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int available() throws IOException {
		return tmp == null ? 0 : tmp.remaining();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new UnsupportedOperationException("mark");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new UnsupportedOperationException("reset");
	}

	@Override
	public long skip(long n) throws IOException {
		throw new IOException(
			new UnsupportedOperationException("skip"));
	}

}
