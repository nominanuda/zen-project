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
package com.nominanuda.web.mvc;

import java.util.LinkedList;
import java.util.List;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataArrayImpl;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.lang.Check;
import com.nominanuda.urispec.StringModelAdapter;

public class DataObjectStringModelAdapter implements StringModelAdapter<DataObject> {
	private final static DataStructHelper helper = new DataStructHelper();

	public List<String> getAsList(DataObject model, String path) {
		LinkedList<String> res = new LinkedList<String>();
		Object val = model.getPathSafe(path);
		if(val == null) {
			return null;
		}
		if(helper.isDataArray(val)) {
			DataArray a = (DataArray)val;
			for(int i : a.getKeys()) {
				while(res.size() < i+1) {
					res.add(null);
				}
				Object v = a.get(i);
				res.set(i, v == null ? null : v.toString());
			}
		} else {
			res.add(val.toString());
		}
		return  res;
	}

	public DataObject createStringModel() {
		return new DataObjectImpl();
	}

	public void setAll(DataObject from, DataObject to) {
		helper.copy(from, to, 
			DataStructHelper.MERGE_POLICY_OVERRIDE);
	}

	public void pushAll(DataObject from, DataObject to) {
		helper.copy(from, to, 
			DataStructHelper.MERGE_POLICY_PUSH);
	}

	public void push(DataObject model, String path, String val) {
		model.setOrPushPathProperty(path, val);
	}

	@Override
	public void set(DataObject model, String path, String val) {
		model.setPathProperty(path, val);
	}

	public boolean validateModel(Object m) {
		return m != null && m instanceof DataObject;
	}

	public void set(DataObject model, String key, List<String> val) {
		DataArray arr = new DataArrayImpl();
		for(String s : val) {
			arr.add(s);
		}
		model.setPathProperty(key, arr);
	}

	public String getFirst(DataObject model, String key) {
		Object o = model.getPathSafe(key);
		if(o == null) {
			return null;
		} else if(helper.isPrimitiveOrNull(o)) {
			return helper.primitiveOrNullToString(o);
		} else if(helper.isDataArray(o)) {
			DataArray a = (DataArray)o;
			if(a.getLength() == 0) {
				return null;
			} else {
				Object v = a.get(0);
				Check.illegalargument.assertTrue(helper.isPrimitiveOrNull(v));
				return v == null
					? null : helper.primitiveOrNullToString(v);
			}
		} else {
			return Check.illegalargument.fail();
		}
	}
}
