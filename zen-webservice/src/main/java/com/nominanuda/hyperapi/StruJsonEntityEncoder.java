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

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.zen.common.Str;
import com.nominanuda.zen.obj.Stru;

public class StruJsonEntityEncoder extends AbstractEntityEncoder<Stru> {

	public StruJsonEntityEncoder() {
		super(Stru.class);
	}

	@Override
	protected HttpEntity encodeInternal(AnnotatedType p, Stru value) {
		byte[] payload = value.toString().getBytes(Str.UTF8);
		ByteArrayEntity e = new ByteArrayEntity(payload);
		e.setContentType(HttpProtocol.CT_APPLICATION_JSON_CS_UTF8);
		return e;
	}
}
