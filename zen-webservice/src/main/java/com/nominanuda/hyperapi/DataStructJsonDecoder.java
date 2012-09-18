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
import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;

public class DataStructJsonDecoder extends AbstractEntityDecoder<DataStruct> {

	public DataStructJsonDecoder() {
		super(DataStruct.class, CT_APPLICATION_JSON);
	}

	@Override
	protected DataStruct decodeInternal(AnnotatedType p, HttpEntity entity) throws IOException {
		String cs = Check.ifNull(HTTP.guessCharset(entity), UTF_8);
		try {
			DataStruct dataEntity = STRUCT.parse(new InputStreamReader(
					entity.getContent(), cs));
			return dataEntity;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
