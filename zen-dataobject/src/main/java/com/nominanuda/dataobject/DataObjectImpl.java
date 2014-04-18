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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataObjectImpl extends AbstractDataStruct<String> implements DataObject{
	private final Map<String, ? super Object> m;

	public DataObjectImpl() {
		super(null);
		m = createInnerMap();
	}

	DataObjectImpl(AbstractDataStruct parent) {
		super(parent);
		m = createInnerMap();
	}
	
	private DataObjectImpl(Map<String, ? super Object> o, DataStruct parent) {
		super(parent);
		m = o;
	}

	public Object put(String k, Object v) {
		Object o = cloneInternal(v,this);
		m.put(checkKey(k), o);
		onMutate();
		return o;
	}
	//Pattern VALID_OBJ_KEY = Pattern.compile("[\\$_A-Za-z][\\$_A-Za-z0-9-]*");
	protected String checkKey(String key) throws IllegalArgumentException {
		try {
			int len = key.length();
			char ch = key.charAt(0);
			if(! (
				(ch >= 'a' && ch <= 'z')||(ch >= 'A' && ch <= 'Z')
				||(ch == '_')||(ch == '$')
			)) {
				throw new IllegalArgumentException();
			}
			for(int i = 1; i < len; i++) {
				ch = key.charAt(i);
				if(! (
					(ch >= 'a' && ch <= 'z')||(ch >= 'A' && ch <= 'Z')||(ch >= '0' && ch <= '9')
					||(ch == '_')||(ch == '$')||(ch == '-')
				)) 
				{
					throw new IllegalArgumentException();
				}
			}
			return key;
		} catch(Exception e) {
			if(e instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)e;
			} else {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public Object get(String k) {
		return m.get(checkKey(k));
	}

	public boolean exists(String k) {
		return m.containsKey(k);
	}
	
	public Object remove(String k) {
		Object o = m.remove(k);
		if(o != null) {
			onMutate();
		}
		return o;
	}
	
	public List<String> getKeys() {
		return new ArrayList<String>(m.keySet());
	}

	public String getType() {
		return DataType.object.name();
	}

	@Override
	protected AbstractDataStruct<String> cloneStruct(
			AbstractDataStruct<?> parent) {
		Map<String, Object> map = createInnerMap();
		DataObjectImpl res = new DataObjectImpl(map, parent);
		for (String k : getKeys()) {
			Object val = get(k);
			if (isPrimitiveOrNull(val)) {
				map.put(k, val);
			} else {
				map.put(k, ((AbstractDataStruct)val).cloneStruct(res));
			}
		}
		return res;
	}

	public DataObject with(String k, Object v) {
		put(k, v);
		return this;
	}
}
