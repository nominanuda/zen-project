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

import static com.nominanuda.zen.common.Maths.MATHS;
import static com.nominanuda.zen.common.Str.UTF8;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.annotation.Nullable;

import com.nominanuda.zen.stereotype.Value;

abstract class BinBacked {
	protected Object javaValue;
	protected final BinRange range;
	protected final JsonType type;

	BinBacked(BinRange range, JsonType type) {
		this.range = range;
		this.type = type;
	}
	BinBacked(BinRange range, JsonType type, @Nullable Object javaValue) {
		this.range = range;
		this.javaValue = javaValue;
		this.type = type;
	}

	BinBacked(@Nullable Object javaValue) {
		this.range = null;
		this.javaValue = javaValue;
		this.type = JsonType.of(javaValue);
	}

	BinRange range() throws IllegalStateException /*if not created from a range*/{
		if(range == null) {
			throw new IllegalStateException("Val not created from I/O");
		}
		return range;
	}

	protected Object getJavaValue() {
		return
				type == JsonType.nil ? null :
				javaValue != null ? javaValue : 
				(javaValue = unmarshal());
		}
	private Object unmarshal() {
		return range.decode(); 
	}

	private String toString;
	@Override
	public String toString() {
		if(toString == null) {
			switch (type) {
			case nil:
				toString = "null";
				break;
			case num:
				toString = MATHS.toString((Number)getJavaValue());
				break;
			default:
				toString = getJavaValue().toString();
				break;
			}
		}
		return toString;
	}

	private int hashCode = 0;
	@Override
	public int hashCode() {
		if(this.hashCode == 0) {
			if(type == JsonType.nil){
				this.hashCode = JsonType.nil.hashCode();
			} else {
				this.hashCode = getJavaValue().hashCode();
			}
		}
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		} else if(obj.getClass().equals(getClass())) {
			return Value.nullSafeEquals(getJavaValue(),((BinBacked)obj).getJavaValue());
		} else {
			return false;
		}
	}

//	public ByteBuffer[] byteBuffer() {
//		if(range == null) {//TODO
//			ByteBuffer bb = UTF8.encode(CharBuffer.wrap(toString()));
//			return new ByteBuffer[] { bb };
//		} else {
//			ByteBuffer bb = UTF8.encode(CharBuffer.wrap(toString()));
//			return new ByteBuffer[] { bb };
//		}
//	}

}
