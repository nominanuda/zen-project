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
package com.nominanuda.hyperapi;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;

import java.io.IOException;
import java.text.NumberFormat;

import okhttp3.ResponseBody;

public class JsonAnyValueEntityDecoder extends AbstractEntityDecoder<Object> {

	public JsonAnyValueEntityDecoder(String contentType) {
		super(Object.class, contentType);
	}
	public JsonAnyValueEntityDecoder() {
		this("application/json");
	}

	@Override
	protected Object decodeInternal(AnnotatedType p, ResponseBody entity) throws IOException {
		String s = entity.string().trim();
		try {
			Class<?> type = p.getType();
			if (Number.class.isAssignableFrom(type)) {
				Number n = NumberFormat.getInstance().parse(s);
				return castNumber(n, type);
			} else {
				return s.startsWith("[") ? new Arr(s) : new Obj(s);
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	private Number castNumber(Number n, Class<?> type) {
		if (Integer.class.equals(type)) {
			return new Integer(n.intValue());
		} else if (Long.class.equals(type)) {
			return new Long(n.longValue());
		} else if (Float.class.equals(type)) {
			return new Float(n.floatValue());
		} else if (Double.class.equals(type)) {
			return new Double(n.doubleValue());
		} else if (Short.class.equals(type)) {
			return new Short(n.shortValue());
		} else if (Byte.class.equals(type)) {
			return new Byte(n.byteValue());
		} else {
			return n;
		}
	}
}
