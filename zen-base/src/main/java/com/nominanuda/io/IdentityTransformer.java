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

import java.io.InputStream;
import java.nio.ByteBuffer;


public class IdentityTransformer extends InputStreamTransformer {

	public IdentityTransformer(InputStream is) {
		super(is);
	}

	@Override
	protected ByteBuffer onEof() {
		return null;
	}

	@Override
	protected ByteBuffer onRead(ByteBuffer b) {
		return b;
	}

	@Override
	protected void onException(Exception e) {
	}

}
