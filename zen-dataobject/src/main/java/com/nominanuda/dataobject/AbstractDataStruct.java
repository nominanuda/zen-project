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

import static com.nominanuda.lang.Check.illegalargument;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Maths;


public abstract class AbstractDataStruct<K> implements DataStruct<K> {
	protected static final DataStructHelper structHelper = new DataStructHelper();
	protected final DataStruct<?> parent;

	public AbstractDataStruct(DataStruct<?> parent) {
		this.parent = parent;
	}
	
	public DataStruct<?> getParent() {
		return parent;
	}

	public DataStruct<?> getRoot() {
		return parent == null
			? this
			: parent.getRoot();
	}

	public DataStruct<K> cloneStruct() {
		return cloneStruct(null);
	}

	public Iterator<K> keyIterator() {
		return getKeys().iterator();
	}

	protected abstract DataStruct<K> cloneStruct(@Nullable AbstractDataStruct<?> parent);
	
	protected Object cloneInternal(Object o, AbstractDataStruct<?> parent) {
		if (o == null) {
			return null;
		} else if (isPrimitiveOrNull(o)) {
			return o;
		} else if (o instanceof AbstractDataStruct) {
			AbstractDataStruct<?> tStruct = (AbstractDataStruct<?>) o;
			return tStruct.cloneStruct(parent);
		}
		throw new IllegalArgumentException();
	}

	protected Map<String, Object> createInnerMap() {
		return new LinkedHashMap<String, Object>();
	}

	protected List<Object> createInnerList() {
		return new LinkedList<Object>();
	}

	public K checkKey(K key) throws IllegalArgumentException {
		illegalargument.assertNotNull(key);
		if(isArray()) {
			illegalargument.assertTrue(
					key instanceof Integer && ((Integer)key) >= 0);
		} else {
			illegalargument.assertTrue(
					VALID_OBJ_KEY.matcher((String)key).matches());
		}
		return key;
	}

	public boolean isPrimitiveOrNull(Object o) {
		return structHelper.isPrimitiveOrNull(o);
	}
	public DataArray asArray() throws ClassCastException {
		return (DataArray)this;
	}

	public DataObject asObject() throws ClassCastException {
		return (DataObject)this;
	}

	public boolean isArray() {
		return this instanceof DataArray;
	}

	public boolean isObject() {
		return this instanceof DataObject;
	}

	public DataObject putNewObject(K key) {
		DataObject o = new DataObjectImpl(this);
		return (DataObject)put(key, o);
	}

	public DataArray putNewArray(K key) {
		DataArray a = new DataArrayImpl(this);
		return (DataArray)put(key, a);
	}
	public Object getStrict(K k) throws NullPointerException {
		return Check.notNull(get(k));
	}

	public DataArray getArray(K k) throws IllegalArgumentException{
		return (DataArray)get(k);
	}

	public DataObject getObject(K k) throws IllegalArgumentException {
		return (DataObject)get(k);
	}

	public void setPathProperty(String path, @Nullable Object value) {
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		explodePath(path).setProperty(pathBits[len-1], value);
	}
	public void setOrPushPathProperty(String path, @Nullable  Object value) {
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		explodePath(path).setOrPushProperty(pathBits[len-1], value);
	}
	@SuppressWarnings("unchecked")
	private DataStruct<Object> explodePath(String path) {
		DataStruct<Object> _target = asObjectKeyedDataStruct();
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		for(int i = 0; i < len - 1; i++) {
			String pathBit = pathBits[i];
			Object k = toStringOrIntKey(pathBit);
			Object newTarget = _target.get(k);
			if(isPrimitiveOrNull(newTarget)) {
				if(Maths.isInteger(pathBits[i + 1])) {
					newTarget = asObjectKeyedDataStruct(_target.putNewArray(k));
				} else {
					newTarget = asObjectKeyedDataStruct(_target.putNewObject(k));
				}
			}
			_target = (DataStruct<Object>)newTarget;
		}
		return _target;
	}

	public void setProperty(Object key, @Nullable Object value) {
		asObjectKeyedDataStruct().put(key, value);
	}
	public void setOrPushProperty(Object key, @Nullable Object value) {
		DataStruct<Object> _this = asObjectKeyedDataStruct();
		if(_this.exists(key)) {
			Object cur = _this.get(key);
			if(structHelper.isDataArray(cur)) {
				((DataArray)cur).add(value);
			} else {
				DataArray darr = new DataArrayImpl();
				darr.add(cur);
				darr.add(value);
				_this.put(key, darr);
			}
		} else {
			setProperty(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	private DataStruct<Object> asObjectKeyedDataStruct() {
		return (DataStruct<Object>)this;
	}
	private Object toStringOrIntKey(String s) {
		Check.notNull(s);
		return Maths.isInteger(s) ? Integer.valueOf(s) : s;
	}
	@SuppressWarnings("unchecked")
	private DataStruct<Object> asObjectKeyedDataStruct(DataStruct<?> ds) {
		return (DataStruct<Object>)ds;
	}
	@Override
	public String toString() {
		return structHelper.toJsonString(this);
	}
	@SuppressWarnings("unchecked")
	public Object getPathSafe(String path) {
		DataStruct<Object> _target = asObjectKeyedDataStruct();
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		for(int i = 0; i < len; i++) {
			String pathBit = pathBits[i];
			Object k = toStringOrIntKey(pathBit);
			Object newTarget = _target.get(k);
			if(isPrimitiveOrNull(newTarget)) {
				return i == len - 1 ? newTarget : null;
			} else {
				_target = (DataStruct<Object>)newTarget;
			}
		}
		return _target;
	}

	public String getString(K key) throws ClassCastException {
		return (String)get(key);
	}

	public Number getNumber(K key) throws ClassCastException {
		return (Number)get(key);
	}

	public Boolean getBoolean(K key) throws ClassCastException {
		return (Boolean)get(key);
	}

	public String getPathSafeString(String path) throws ClassCastException {
		return (String)getPathSafe(path);
	}

	public Number getPathSafeNumber(String path) throws ClassCastException {
		return (Number)getPathSafe(path);
	}

	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		return (Boolean)getPathSafe(path);
	}

	public DataObject getPathSafeObject(String path) throws ClassCastException {
		return (DataObject)getPathSafe(path);
	}

	public DataArray getPathSafeArray(String path) throws ClassCastException {
		return (DataArray)getPathSafe(path);
	}

	public Long getLong(K key) throws ClassCastException {
		Number n = getNumber(key);
		return n == null ? null : (Long)n;
	}

	public Double getDouble(K key) throws ClassCastException {
		Number n = getNumber(key);
		return n == null ? null : (Double)n;
	}

	public String getStrictString(K key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getString(key));
	}

	public Long getStrictLong(K key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getLong(key));
	}

	public Double getStrictDouble(K key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getDouble(key));
	}

	public Boolean getStrictBoolean(K key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getBoolean(key));
	}

	public DataObject getStrictObject(K key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getObject(key));
	}

	public DataArray getStrictArray(K key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getArray(key));
	}

}
