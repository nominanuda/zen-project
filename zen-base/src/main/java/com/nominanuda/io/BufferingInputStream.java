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


public class BufferingInputStream extends InputStream {
	private InputStream is;
	private AppendableByteBuffer buf;
	private boolean isConsumed = false;

	public BufferingInputStream(InputStream is) {
		this(is, AppendableByteBuffer.HEAP_BASED, 1000000, AppendableByteBuffer.ALLOCATION_POLICY_LINEAR);
	}
	public BufferingInputStream(InputStream is, int backend, int bufSize, int bufAllocationPolicy) {
		switch (backend) {
		case AppendableByteBuffer.HEAP_BASED:
			buf = new HeapAppendableByteBuffer(bufSize, bufAllocationPolicy);
			break;
		case AppendableByteBuffer.MMAP_BASED:
			buf = new MMapAppendableByteBuffer(bufSize, bufAllocationPolicy);
			break;
		default:
			throw new IllegalArgumentException("unknown type of backend");
		}
		this.is = is;
	}
	private boolean isNextFromIs() {
		return buf.remaining() == 0 && ! isConsumed;
	}

	@Override
	public int available() throws IOException {
		return isNextFromIs() ? is.available() : buf.limit() - buf.position() - 1;
	}

	@Override
	public void close() {
		buf.dispose();
	}

	private void closeSource() throws IOException {
		if(! isConsumed) {
			is.close();
			isConsumed = true;
		}
	}
	@Override
	protected void finalize() throws Throwable {
		buf.dispose();
	};
	@Override
	public synchronized void mark(int readlimit) {
		if(readlimit >= buf.limit()) {
			throw new IllegalArgumentException("illegal attempt to position mark over stream end");
		}
		buf.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return true;
	}

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
		return isNextFromIs() ? readFromStream(b, off, len) : readFromBuffer(b, off, len);
	}

	public int readFromStream(byte[] b, int off, int len) throws IOException {
		int res = is.read(b, off, len);
		if(res == 0) {
			throw new IllegalStateException("read returned 0");
		} else if(res < 0) {
			closeSource();
			return -1;
		}
		buf.append(b, off, res);
		buf.position(buf.limit() - 1);
		return res;
	}
	public int readFromBuffer(byte[] b, int off, int len) throws IOException {
		return buf.get(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public synchronized void reset() throws IOException {
		buf.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return 0;
	}
	
}
