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

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataArrayImpl;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.lang.Check;

public class DataStructContentHandler implements JsonContentHandler {
	private final DataStructHelper dataStructHelper = new DataStructHelper();
	private volatile boolean finished = false;
	private DataStruct result;
	private DataStruct cur;
	private String pendingKey;
//	private Object pendingValue;

	public void startJSON() throws RuntimeException {
	}

	public void endJSON() throws RuntimeException {
		finished = true;
	}

	public boolean startObject() throws RuntimeException {
		if(result == null) {
			result = new DataObjectImpl();
			cur = result;
		} else {
			if(dataStructHelper.isDataArray(cur)) {
				cur = ((DataArray)cur).addNewObject();
			} else {
				cur = ((DataObject)cur).putNewObject(pendingKey);
			}
		}
		return true;
	}

	public boolean endObject() throws RuntimeException {
		cur = cur.getParent();
		return true;
	}

	public boolean startObjectEntry(String key) throws RuntimeException {
		pendingKey = key;
		return true;
	}

	public boolean endObjectEntry() throws RuntimeException {
		return true;
	}

	public boolean startArray() throws RuntimeException {
		if(result == null) {
			result = new DataArrayImpl();
			cur = result;
		} else {
			if(dataStructHelper.isDataArray(cur)) {
				cur = ((DataArray)cur).addNewArray();
			} else {
				cur = ((DataObject)cur).putNewArray(pendingKey);
			}
		}
		return true;
	}

	public boolean endArray() throws RuntimeException {
		cur = cur.getParent();
		return true;
	}

	public boolean primitive(Object value) throws RuntimeException {
		Check.illegalargument.assertTrue(dataStructHelper.isPrimitiveOrNull(value));
		if(dataStructHelper.isDataArray(cur)) {
			((DataArray)cur).add(value);
		} else {
			((DataObject)cur).put(pendingKey, value);
		}
		return true;
	}

	public DataStruct getResult() {
		Check.illegalstate.assertTrue(finished);
		return result;
	}
}
