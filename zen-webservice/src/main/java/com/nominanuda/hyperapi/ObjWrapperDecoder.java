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

import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;
import com.nominanuda.zen.obj.wrap.Wrap;

public class ObjWrapperDecoder extends AbstractEntityDecoder<ObjWrapper> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public ObjWrapperDecoder(String contentType) {
		super(ObjWrapper.class, contentType);
	}

	public ObjWrapperDecoder() {
		this(CT_APPLICATION_JSON);
	}

	@Override
	protected ObjWrapper decodeInternal(AnnotatedType p, HttpEntity entity) throws IOException {
		String s = IO.readAndCloseUtf8(entity.getContent());
		try {
			if ("null".equals(s)) {
				return null;
			} else {
				Obj ds = Obj.parse(new StringReader(s));
				return (ObjWrapper)Wrap.WF.wrap(ds, p.getType());
			}
		} catch (Exception e) {
			log.error("error trying to parse: " + s);
			throw e;
		}
	}
}
