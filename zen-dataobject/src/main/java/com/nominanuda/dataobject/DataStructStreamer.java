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

import com.nominanuda.code.Immutable;

@Immutable
public class DataStructStreamer implements JsonStreamer {
	private DataStructHelper structHelper = new DataStructHelper(); 
	private final DataStruct struct;

	public DataStructStreamer(DataStruct struct) {
		this.struct = struct;
	}

	public static void stream(DataStruct struct, JsonContentHandler ch) throws RuntimeException {
		new DataStructStreamer(struct).stream(ch);
	}

	public void stream(JsonContentHandler jch) throws RuntimeException {
		jch.startJSON();
		streamItem(jch, struct);
		jch.endJSON();
	}

	private void stream(Arr array, JsonContentHandler jch) throws RuntimeException {
		jch.startArray();
		int len = array.getLength();
		for(int i = 0; i < len; i++) {
			Object o = array.get(i);
			streamItem(jch, o);
		}
		jch.endArray();
	}

	private void streamItem(JsonContentHandler jch, Object o) throws RuntimeException {
		DataType dt = structHelper.getDataType(o);
		switch (dt) {
		case array:
			stream((Arr)o, jch);
			break;
		case object:
			stream((Obj)o, jch);
			break;
		default:
			jch.primitive(o);
			break;
		}
	}
	private void stream(Obj object, JsonContentHandler jch) throws RuntimeException {
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
