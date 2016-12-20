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
package com.nominanuda.web.mvc;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.HttpProtocol;

public class JsonEntity extends StringEntity implements HttpProtocol {
	public JsonEntity(DataStruct json, String charset, String contentType) throws UnsupportedEncodingException {
		super(parseStringContent(STRUCT.toJsonString(Check.notNull(json))), charset);
		setContentType(contentType);
	}
	public JsonEntity(DataStruct json, String contentType) throws UnsupportedEncodingException {
		this(json, "UTF-8", contentType);
	}
	public JsonEntity(DataStruct json) throws UnsupportedEncodingException {
		this(json, CT_APPLICATION_JSON);
	}
	
	private static String parseStringContent(String token) {
		StringBuilder b = new StringBuilder();
		char[] ch = token.toCharArray();
		int len = ch.length;
		for (int i = 0; i < len; i++) {
			char c = ch[i];
			if ('\t' == c) {
				b.append("\\t");
			} else if ('\r' != c && 0x0b != c) {
				b.append(c);
			}
		}
		return b.toString();
	}
}
