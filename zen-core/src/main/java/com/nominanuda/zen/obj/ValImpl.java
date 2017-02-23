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
package com.nominanuda.zen.obj;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

class ValImpl extends BinBacked implements Val {

	public ValImpl(BinRange range, JsonType t) {
		super(range, t);
	}
	public ValImpl(BinRange range, JsonType t, @Nullable Object javaValue) {
		super(range, t, javaValue);
	}

	public ValImpl(@Nullable Object v) {
		super(v);
	}
	@Override
	public JsonType getType() {
		return type;
	}

	@Override
	public Object get() {
		return getJavaValue();
	}
	@Override
	public ByteBuffer[] byteBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object toJavaObjModel() {
		return getJavaValue();
	}

}
