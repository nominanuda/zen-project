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
 * 
 */
package com.nominanuda.dataobject;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.nominanuda.code.Immutable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.xml.SAXEmitter;

@Immutable
public class DataStructSAXStreamer implements SAXEmitter {
	private final DataStruct struct;

	public DataStructSAXStreamer(DataStruct struct) {
		this.struct = struct;
	}

	@ThreadSafe
	public static void toSAX(DataStruct struct, ContentHandler ch) throws SAXException {
		new DataStructSAXStreamer(struct).toSAX(ch);
	}

	public void toSAX(ContentHandler ch) throws SAXException {
		JsonSaxAdapter jsa = new JsonSaxAdapter(ch);
		try {
			new DataStructStreamer(struct).stream(jsa);
		} catch(Exception e) {
			throw new SAXException(e);
		}
	}
}
