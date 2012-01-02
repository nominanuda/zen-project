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
package com.nominanuda.dataobject.jsonparser;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.nominanuda.code.Immutable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.DataType;

@Immutable @ThreadSafe
public class DataStructSAXStreamer {
	private DataStructHelper structHelper = new DataStructHelper(); 

	public void toSAX(DataStruct<?> struct, ContentHandler ch) throws SAXException {
		JsonSaxAdapter jsa = new JsonSaxAdapter(ch);
		jsa.startJSON();
		streamItem(jsa, struct);
		jsa.endJSON();
	}

	private void stream(DataArray array, JsonContentHandler jch) throws SAXException {
		jch.startArray();
		int len = array.getLength();
		for(int i = 0; i < len; i++) {
			Object o = array.get(i);
			streamItem(jch, o);
		}
		jch.endArray();
	}

	private void streamItem(JsonContentHandler jch, Object o) throws SAXException {
		DataType dt = structHelper.getDataType(o);
		switch (dt) {
		case array:
			stream((DataArray)o, jch);
			break;
		case object:
			stream((DataObject)o, jch);
			break;
		default:
			jch.primitive(o);
			break;
		}
	}
	private void stream(DataObject object, JsonContentHandler jch) throws SAXException {
		jch.startObject();
		for(String k : object.getKeys()) {
			Object o = object.get(k);
			jch.startObjectEntry(k);
			streamItem(jch, o);
			jch.endObjectEntry();
		}
		jch.endObject();
	}
}
