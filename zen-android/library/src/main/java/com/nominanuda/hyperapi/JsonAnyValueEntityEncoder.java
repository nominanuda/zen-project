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

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class JsonAnyValueEntityEncoder extends AbstractEntityEncoder<Object> {
	private static final MediaType MT = MediaType.parse("application/json; charset=UTF-8");

	public JsonAnyValueEntityEncoder() {
		super(Object.class);
	}

	@Override
	protected RequestBody encodeInternal(AnnotatedType p, Object value) {
		return RequestBody.create(MT, value != null ? value.toString() : "null");
	}
}
