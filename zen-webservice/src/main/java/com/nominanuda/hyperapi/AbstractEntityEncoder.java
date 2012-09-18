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

import com.nominanuda.web.http.HttpProtocol;


public abstract class AbstractEntityEncoder<T> implements EntityEncoder, HttpProtocol {
	private final Class<T> cl;

	public AbstractEntityEncoder(Class<T> cl) {
		this.cl = cl;
	}

	protected String defaultContentType = CT_APPLICATION_OCTET_STREAM;
	protected abstract HttpEntity encodeInternal(AnnotatedType p, T value);

	@Override
	public boolean supports(AnnotatedType p, Object value) {
		return value == null || p.isAssignableFrom(cl) && cl.isAssignableFrom(value.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public HttpEntity encode(AnnotatedType p, Object value) {
		return value == null ?  null : encodeInternal(p, (T)value);
	}

}
