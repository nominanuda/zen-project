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
import java.util.List;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;


public class DataArrayImpl extends AbstractDataStruct<Integer> implements DataArray {
	private List<Object> l;

	public DataArrayImpl() {
		super(null);
		l = createInnerList();
	}

	DataArrayImpl(AbstractDataStruct parent) {
		super(parent);
		l = createInnerList();
	}

	private DataArrayImpl(List<Object> o, AbstractDataStruct parent) {
		super(parent);
		l = o;
	}

	public int getLength() {
		return l.size();
	}

	public Object put(Integer i, Object v) {
		Object obj = isPrimitiveOrNull(v)
			? v : ((AbstractDataStruct)v).cloneStruct(this);
		l.set(ensureRoom(i),obj);
		return obj;
	}

	public Object get(Integer i) {
		return i >= l.size() ? null : l.get(i);
	}

	private int ensureRoom(int i) {
		int len = l.size();
		if(i >= len) {
			int howMany = i - len + 1;
			for(int j = 0; j < howMany; j++) {
				l.add(null);
			}
		}
		return i;
	}

	public Object remove(Integer i) {
		return l.remove(i);
	}

	public Object add(Object v) {
		return put(getLength(), v);
	}

	public DataObject addNewObject() {
		return putNewObject(getLength());
	}

	public DataArray addNewArray() {
		return putNewArray(getLength());
	}

	public String getType() {
		return DataType.array.name();
	}

	@Override
	protected AbstractDataStruct<Integer> cloneStruct(
			@Nullable AbstractDataStruct parent) {
		List<Object> list = createInnerList();
		DataArrayImpl res = new DataArrayImpl(list, parent);
		int len = getLength();
		for (int i = 0; i < len; i++) {
			Object o = get(i);
			if (isPrimitiveOrNull(o)) {
				list.add(o);
			} else {
				list.add(((AbstractDataStruct)o).cloneStruct(res));
			}
		}
		return res;
	}

	public boolean exists(Integer key) {
		Check.notNull(key);
		Check.illegalargument.assertTrue(key instanceof Integer);//TODO remove
		int k = (Integer)key;
		Check.illegalargument.assertFalse(k < 0);
		return getLength() > k;
	}

	public List<Integer> getKeys() {
		int len = getLength();
		ArrayList<Integer> l = new ArrayList<Integer>(len);
		for(int i = 0; i < len; i++) {
			l.add(i);
		}
		return l;
	}

	@Override
	public Iterator<Object> iterator() {
		return l.iterator();
	}
}