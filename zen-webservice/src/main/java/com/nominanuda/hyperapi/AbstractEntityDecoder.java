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

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.HttpProtocol;

public abstract class AbstractEntityDecoder<T> implements EntityDecoder, HttpProtocol {
	protected static final String ANY_CONTENT_TYPE = "*";
	private final Class<T> cl;
	protected final String contentType;

	protected abstract T decodeInternal(AnnotatedType p, HttpEntity value) throws IOException;

	public AbstractEntityDecoder(Class<T> cl, @Nullable String ct) {
		this.cl = cl;
		this.contentType = Check.ifNull(ct, ANY_CONTENT_TYPE).replace(" ", "");
	}

	@Override
	public boolean supports(AnnotatedType p, HttpEntity entity) {
		String ct = Check.ifNullOrBlank(getContentType(entity), CT_APPLICATION_OCTET_STREAM);
		return contentTypeMatches(ct) && p.isAssignableFrom(cl);
	}

	@Override
	public Object decode(AnnotatedType p, HttpEntity entity) throws IOException {
		return decodeInternal(p, entity);
	}

	private boolean contentTypeMatches(String ct) {
		return ANY_CONTENT_TYPE.equals(contentType) || ct.replace(" ", "").startsWith(contentType);
	}

	protected @Nullable String getContentType(HttpEntity entity) {
		Header cth  = entity.getContentType();
		return cth == null ? null : cth.getValue();
	}

}
