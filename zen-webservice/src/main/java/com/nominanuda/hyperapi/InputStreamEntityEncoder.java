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
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import com.nominanuda.lang.Check;

import static com.nominanuda.io.IOHelper.IO;

public class InputStreamEntityEncoder extends AbstractEntityEncoder<InputStream> {

	public InputStreamEntityEncoder() {
		super(InputStream.class);
	}

	@Override
	protected HttpEntity encodeInternal(AnnotatedType p, InputStream value) {
		try {
			byte[] barr = IO.read(value, true);
			String ct = Check.ifNullOrBlank(p.mediaType(), defaultContentType);
			ByteArrayEntity entity = new ByteArrayEntity(barr);
			entity.setContentType(ct);
			return entity;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
