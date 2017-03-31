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

import com.nominanuda.zen.common.Util;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

import static com.nominanuda.zen.common.Str.UTF8;

public abstract class AbstractEntityDecoder<T> implements EntityDecoder {
	public static final String ANY_CONTENT_TYPE = null;
	private static final MediaType FALLBACK_MT = MediaType.parse("application/octet-stream");

	private final Class<T> cl;
	private final MediaType mediaType;

	protected abstract T decodeInternal(AnnotatedType p, ResponseBody value) throws IOException;

	public AbstractEntityDecoder(Class<T> cl, @Nullable String ct) {
		this.cl = cl;
		this.mediaType = ct != null ? MediaType.parse(ct) : null;
	}

	@Override
	public boolean supports(AnnotatedType p, ResponseBody entity) {
		MediaType mt = Util.notNullElse(getMediaType(entity), FALLBACK_MT);
		return mediaTypeMatches(mt) && p.isAssignableTo(cl);
	}

	@Override
	public Object decode(AnnotatedType p, ResponseBody entity) throws IOException {
		return decodeInternal(p, entity);
	}

	private boolean mediaTypeMatches(MediaType mt) {
		return mediaType == null // accepts everything
			|| mediaType.type().equals(mt.type())
			&& mediaType.subtype().equals(mt.subtype())
			&& mediaType.charset(UTF8).equals(mt.charset(UTF8));
	}

	protected @Nullable MediaType getMediaType(ResponseBody entity) {
		return entity.contentType();
	}

}
