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

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static com.nominanuda.io.IOHelper.IO;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Maths;

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
			if ("null".equals(s)) {
				return null;
			} else if ("true".equals(s) || "false".equals(s)) {
				return Boolean.valueOf(s);
			} else if (Maths.isNumber(s)) {
				if (Maths.isInteger(s)) { // don't use ?: operator or it will always return Double!!!
					return Long.valueOf(s);
				} else {
					return Double.valueOf(s);
				}
			} else if (s.startsWith("\"") && s.length() > 1) {
				return STRUCT.jsonStringUnescape(s.substring(1, s.length() - 1));
			} else {
				DataStruct ds = STRUCT.parse(new StringReader(s));
				return ds;
			}
		} catch (Exception e) {
			log.error("error trying to parse: " + s);
			throw e;
		}
	}
}
