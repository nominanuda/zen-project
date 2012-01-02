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
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public abstract class AppendableByteBuffer {
	public static final int ALLOCATION_POLICY_LINEAR = 0;
	public static final int ALLOCATION_POLICY_DOUBLING = 1;
	public static final int HEAP_BASED = 0;
	public static final int MMAP_BASED = 1;
	private int pos = 0;
	private int mark = 0;
	private int limit = 1;
	private boolean disposed = false;
	private boolean invalid = false;

	protected abstract  ByteBuffer[] getBufs();
	protected abstract void onDispose();
	protected abstract ByteBuffer createBuf() throws IOException;

	@Override
	protected void finalize() throws Throwable {
		dispose();
	}

	public final void dispose() {
		if(! disposed) {
			onDispose();
		}
		invalid = true;
	}
	
	protected void check() {
		if(invalid) {
			throw new IllegalStateException("invalidated");
		}
	}
	
	public ByteBuffer[] exportForDraining() {
		check();
		ByteBuffer[] bufs = getBufs();
		invalid  = true;
		return bufs;
	}
	
	public int limit() {
		check();
		return limit;
	}

	public int position() {
		check();
		return pos;
	}

	public void position(int p) {
		check();
		if(p >= limit) {
			throw new BufferOverflowException();
		}
		pos = p;
	}

	public void reset() {
		check();
		position(mark);
	}

	public int remaining() {
		check();
		return limit - pos - 1;
	}

	public void mark(int readlimit) {
		check();
		mark = pos;
	}

	public void append(byte[] src, int offset, int length) throws IOException {
		check();
		int stillToCopy = length;
		int off = offset;
		while(stillToCopy > 0) {
			ByteBuffer bb = getOrCreateBuf();
			int numToCopy = Math.min(stillToCopy, bb.remaining());
			bb.put(src, off, numToCopy);
			stillToCopy -= numToCopy;
			off += numToCopy;
			limit += numToCopy;
		}
	}


	public int get(byte[] dst, int offset, int length) {
		check();
		final int totToCopy = Math.min(length, remaining());
		int stillToCopy = totToCopy;
		int off = offset;
		while(stillToCopy > 0) {
			int[] rPos = getReadPointer();
			ByteBuffer src = getBufs()[rPos[0]];
			int oldBufPos = src.position();
			int oldBufLimit = src.limit();
			src.flip();
			src.position(rPos[1]);
			int numToCopy = Math.min(stillToCopy, src.remaining());
			src.get(dst, off, numToCopy);
			off += numToCopy;
			stillToCopy -= numToCopy;
			pos += numToCopy;
			src.position(oldBufPos);
			src.limit(oldBufLimit);
		}
		return totToCopy == 0 ? -1 : totToCopy;
	}

	private int[] getReadPointer() {
		check();
		int p = pos;
		int bufNum = 0;
		ByteBuffer[] bufs = getBufs();
		for(int i = 0; i < bufs.length; i++) {
			final ByteBuffer bb = bufs[i];
			int capacity = bb.capacity();
			if(p >= capacity) {
				p -= capacity;
			} else {
				bufNum = i;
				break;
			}
		}
		return new int[] {bufNum, p};
	}
	protected ByteBuffer getOrCreateBuf() throws IOException {
		ByteBuffer[] bufs = getBufs();
		if(bufs.length == 0) {
			return createBuf();
		}
		ByteBuffer lastBuf = bufs[bufs.length -1];
		if(lastBuf.remaining() == 0) {
			return createBuf();
		}
		return lastBuf;
	}

}