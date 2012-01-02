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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class MMapAppendableByteBuffer extends AppendableByteBuffer {
	private List<MappedByteBuffer> mmbbs = new LinkedList<MappedByteBuffer>();
	private List<RandomAccessFile> files = new LinkedList<RandomAccessFile>();
	private List<File> tmpFiles = new LinkedList<File>();
	private List<FileChannel> fileChannels = new LinkedList<FileChannel>();
	private int sizeToAllocate = new Double(Math.pow(2, 20)).intValue() * 10;//10M
	private int allocationPolicy = ALLOCATION_POLICY_LINEAR;

	public MMapAppendableByteBuffer() {
	}
	public MMapAppendableByteBuffer(int initialBufSize, int allocationPolicy) {
		sizeToAllocate = initialBufSize;
		this.allocationPolicy  = allocationPolicy;
	}
	@Override
	protected void onDispose() {
		int len = files.size();
		for(int i = 0; i < len; i++) {
			try {
				files.get(i).close();
				tmpFiles.get(i).delete();
				fileChannels.get(i).close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	@Override
	protected ByteBuffer[] getBufs() {
		return mmbbs.toArray(new ByteBuffer[mmbbs.size()]);
	}
	@Override
	protected MappedByteBuffer createBuf() throws IOException {
		File tempFile = File.createTempFile(
			"mmapbuffer_" + UUID.randomUUID().toString(), null);
		RandomAccessFile file = new RandomAccessFile(tempFile, "rw");
		FileChannel channel = file.getChannel();
		int size = sizeToAllocate;
		if(allocationPolicy == ALLOCATION_POLICY_DOUBLING) {
			sizeToAllocate *= 2;
		}
		MappedByteBuffer mbb = channel.map(MapMode.PRIVATE, 0, size);
		files.add(file);
		tmpFiles.add(tempFile);
		fileChannels.add(channel);
		mmbbs.add(mbb);
		return mbb;
	}
}
