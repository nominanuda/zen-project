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

import static com.nominanuda.zen.obj.JsonDeserializer.JSON_DESERIALIZER;
import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonAnyValueDecoder extends AbstractEntityDecoder<Object> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public JsonAnyValueDecoder(String contentType) {
		super(Object.class, contentType);
	}
	public JsonAnyValueDecoder() {
		this(CT_APPLICATION_JSON);
	}

	@Override
	protected Object decodeInternal(AnnotatedType p, HttpEntity entity) throws IOException {
		String s = IO.readAndCloseUtf8(entity.getContent());
		try {
			Object res = JSON_DESERIALIZER.deserialize(s);
			Class<?> type = p.getType();
			if(Number.class.isAssignableFrom(type)) {
				Number n = (Number)res;
				return castNumber(n, type);
			} else {
				return res;
			}
		} catch (Exception e) {
			log.error("error trying to parse: " + s);
			throw e;
		}
	}
	private Number castNumber(Number n, Class<?> type) {
		if(Integer.class.equals(type)) {
			return new Integer(n.intValue());
		} else if(Long.class.equals(type)) {
			return new Long(n.longValue());
		} else if(Float.class.equals(type)) {
			return new Float(n.floatValue());
		} else if(Double.class.equals(type)) {
			return new Double(n.doubleValue());
		} else if(Short.class.equals(type)) {
			return new Short(n.shortValue());
		} else if(Byte.class.equals(type)) {
			return new Byte(n.byteValue());
		} else {
			return n;
		}
	}
}
