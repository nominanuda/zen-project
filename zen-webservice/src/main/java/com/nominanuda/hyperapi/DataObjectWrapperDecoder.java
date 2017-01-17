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
import static com.nominanuda.dataobject.WrappingFactory.WF;
import static com.nominanuda.io.IOHelper.IO;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.dataobject.DataObjectWrapper;
import com.nominanuda.dataobject.DataStruct;

public class DataObjectWrapperDecoder extends AbstractEntityDecoder<DataObjectWrapper> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public DataObjectWrapperDecoder(String contentType) {
		super(DataObjectWrapper.class, contentType);
	}
	public DataObjectWrapperDecoder() {
		this(CT_APPLICATION_JSON);
	}

	@Override
	protected DataObjectWrapper decodeInternal(AnnotatedType p, HttpEntity entity) throws IOException {
		String s = IO.readAndCloseUtf8(entity.getContent());
		try {
			if ("null".equals(s)) {
				return null;
			} else {
				DataStruct ds = STRUCT.parse(new StringReader(s));
				return (DataObjectWrapper)WF.wrap(ds.asObject(), p.getType());
			}
		} catch (Exception e) {
			log.error("error trying to parse: " + s);
			throw e;
		}
	}
}
