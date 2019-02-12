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

import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;

import java.io.IOException;

import okhttp3.ResponseBody;

import static com.nominanuda.zen.obj.wrap.Wrap.WF;

public class ObjWrapperEntityDecoder extends AbstractEntityDecoder<ObjWrapper> {
	public ObjWrapperEntityDecoder(String contentType) {
		super(ObjWrapper.class, contentType);
	}

	public ObjWrapperEntityDecoder() {
		this("application/json");
	}

	@Override
	protected ObjWrapper decodeInternal(AnnotatedType p, ResponseBody entity) throws IOException {
		String s = entity.string().trim();
		try {
			if ("null".equals(s)) {
				return null;
			} else {
				Obj ds = new Obj(s);
				return (ObjWrapper) WF.wrap(ds, p.getType());
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
