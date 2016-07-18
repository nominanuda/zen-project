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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nominanuda.lang.Check;

public class DataObjectImpl extends AbstractDataStruct<String> implements DataObject{
	private final Map<String, ? super Object> m;

	public DataObjectImpl() {
		super(null);
		m = createInnerMap();
	}

	DataObjectImpl(AbstractDataStruct<?> parent) {
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
	protected String checkKey(String key) throws IllegalArgumentException {
		return key;
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
	protected AbstractDataStruct<String> cloneStruct(AbstractDataStruct<?> parent) {
		Map<String, Object> map = createInnerMap();
		DataObjectImpl res = new DataObjectImpl(map, parent);
		for (String k : getKeys()) {
			Object val = get(k);
			if (isPrimitiveOrNull(val)) {
				map.put(k, val);
			} else {
				map.put(k, ((AbstractDataStruct<?>)val).cloneStruct(res));
			}
		}
		return res;
	}

	public DataObject with(String k, Object v) {
		put(k, v);
		return this;
	}

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return iteratorOf(this);
	}

	public static Iterator<Entry<String, Object>> iteratorOf(DataObject o) {
		List<Entry<String, Object>> l = new LinkedList<Map.Entry<String,Object>>();
		for(final String k : o.getKeys()) {
			final Object v = o.get(k);
			l.add(new Entry<String, Object>() {
				public Object setValue(Object arg0) {
					Check.unsupportedoperation.fail();
					return null;
				}
				public Object getValue() {
					return v;
				}
				public String getKey() {
					return k;
				}
			});
		}
		return l.iterator();
	}
}
