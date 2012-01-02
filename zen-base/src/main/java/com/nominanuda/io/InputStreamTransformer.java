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


public abstract class InputStreamTransformer extends InputStream {
	private InputStream source;
	private AppendableByteBuffer pending = new HeapAppendableByteBuffer();
	private boolean eofOccurred = false;

	protected abstract ByteBuffer onRead(ByteBuffer b) throws IOException;
	protected abstract ByteBuffer onEof() throws IOException;
	protected abstract void onException(Exception e) throws Exception;

	public InputStreamTransformer(InputStream is) {
		if(source != null) {
			throw new IllegalStateException();
		}
		source = is;
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			if(! eofOccurred) {
				if(pending.remaining() > 0) {
					return flushPending(b, off, len);
				}
				ByteBuffer b1 = ByteBuffer.allocate(len);
				int numRead = source.read(b1.array(), 0, len);
				switch (numRead) {
				case 0:
					throw new IllegalStateException("read returned 0");
				case -1:
					eofOccurred = true;
					ByteBuffer flushBuf = onEof();
					if(flushBuf != null) {
						flushBuf.flip();
						pending.append(flushBuf.array(),flushBuf.position(), flushBuf.remaining());
					}
					return flushPending(b, off, len);
				default:
					b1.position(numRead);
					ByteBuffer outBuf = onRead(b1);
					if(outBuf == null) {//that is if onRead returns null carry on with given data
						outBuf = b1;
					}
					outBuf.flip();
					if(outBuf.remaining() > len) {
						outBuf.get(b, off, len);
						if(outBuf.hasRemaining()) {
							pending.append(outBuf.array(),outBuf.position(), outBuf.remaining());
						}
						return len;
					} else {
						int howMany = outBuf.remaining();
						outBuf.get(b, off, howMany);
						return howMany;
					}
				}
			} else {
				return flushPending(b, off, len);
			}
		} catch(Exception e) {
			throwException(e);
			/*never */return 0;
		}
	}
	private void throwException(Exception e) throws IOException {
		IOException ioe = e instanceof IOException ? (IOException)e : new IOException(e);
		try {
			onException(e);
		} catch(Exception e1) {
			throw new IOException(e1);
		}
		throw ioe;
	}
	private int flushPending(byte[] b, int off, int len) {
		if(pending.remaining() > 0) {
			int num = Math.min(pending.remaining(), len);
			pending.get(b, off, num);
			return num;
		} else {
			return -1;
		}
	}
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	@Override
	public int available() throws IOException {
		try {
			return source.available();
		} catch(Exception e) {
			throwException(e);
			/*never */return 0;
		}
	}
	@Override
	public void close() throws IOException {
		try {
			source.close();
		} catch(Exception e) {
			throwException(e);
		}
	}
	@Override
	public synchronized void mark(int readlimit) {
		source.mark(readlimit);
	}
	@Override
	public boolean markSupported() {
		return source.markSupported();
	}
	@Override
	public synchronized void reset() throws IOException {
		source.reset();
	}
	@Override
	public long skip(long n) throws IOException {
		try {
			return source.skip(n);
		} catch(Exception e) {
			throwException(e);
			/*never */return 0;
		}
	}
	@Override
	public int read() throws IOException {
		throwException(new RuntimeException());
		return 0;
	}

}
