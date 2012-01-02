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
import java.nio.channels.WritableByteChannel;

public class TeeInputStreamTransformer extends InputStreamTransformer {
	private WritableByteChannel ch;

	public TeeInputStreamTransformer(InputStream is, WritableByteChannel ch) throws IOException {
		super(is);
		this.ch = ch;
	}

	@Override
	protected ByteBuffer onEof() throws IOException {
		ch.close();
		return null;
	}

	@Override
	protected void onException(Exception e) throws Exception {
		ch.close();
	}

	@Override
	protected ByteBuffer onRead(ByteBuffer b) throws IOException {
		ch.write(b.duplicate());
		return b;
	}
}
