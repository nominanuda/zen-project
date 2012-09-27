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
package com.nominanuda.dataobject;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;

/**
 * readonly
 *
 */
public class MapsAndListsObjectDecorator extends AbstractMap<String, Object> {
	private final static DataStructHelper dataStructHelper = new DataStructHelper();
	private DataObject o;

	public static @Nullable Object wrapIfNecessary(@Nullable Object v) {
		if(dataStructHelper.isDataArray(v)) {
			return new MapsAndListsArrayDecorator((DataArray)v);
		} else if(dataStructHelper.isDataObject(v)) {
			return new MapsAndListsObjectDecorator((DataObject)v);
		} else {
			return v;
		}
	}

	public MapsAndListsObjectDecorator(DataObject obj) {
		o = obj;
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		LinkedHashSet<java.util.Map.Entry<String, Object>> res = new LinkedHashSet<Map.Entry<String,Object>>();
		for(String k : o.getKeys()) {
			res.add(new MyEntry(k, o.get(k)));
		}
		return Collections.unmodifiableSet(res);
	}

	private class MyEntry implements java.util.Map.Entry<String, Object> {
		private String k;
		private Object v;
		public MyEntry(String k, Object v) {
			this.k = k;
			this.v = v;
		}
		public String getKey() {
			return k;
		}
		public Object getValue() {
			return wrapIfNecessary(v);
		}
		public Object setValue(Object value) {
			return Check.unsupportedoperation.fail("readonly object");
		}
	}

	public DataObject unwrap() {
		return o;
	}
}
