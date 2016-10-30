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


import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Maths;


abstract class AbstractDataStruct<K> implements DataStruct, PropertyBag<K> {
	protected final DataStruct parent;
	private String json = null;
	

	AbstractDataStruct(DataStruct parent) {
		this.parent = parent;
	}
	
//	public DataStruct getParent() {
//		return parent;
//	}
//
//	@SuppressWarnings("deprecation")
//	public DataStruct getRoot() {
//		return parent == null
//			? this
//			: parent.getRoot();
//	}

	public DataStruct cloneStruct() {
		return cloneStruct(null);
	}

	@Override
	public Iterator<K> keyIterator() {
		return getKeys().iterator();
	}

	protected abstract DataStruct cloneStruct(@Nullable AbstractDataStruct<?> parent);
	
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

/*
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
*/
	@Override
	public boolean isPrimitiveOrNull(Object o) {
		return STRUCT.isPrimitiveOrNull(o);
	}
	
	@Override
	public DataArray asArray() throws ClassCastException {
		return (DataArray)this;
	}

	@Override
	public DataObject asObject() throws ClassCastException {
		return (DataObject)this;
	}

	@Override
	public boolean isArray() {
		return this instanceof DataArray;
	}

	@Override
	public boolean isObject() {
		return this instanceof DataObject;
	}

	@Override
	public DataObject putNewObject(K key) {
		DataObject o = new DataObjectImpl(this);
		return (DataObject)put(key, o);
	}

	@Override
	public DataArray putNewArray(K key) {
		DataArray a = new DataArrayImpl(this);
		return (DataArray)put(key, a);
	}
	
	@Override
	public Object getStrict(K k) throws NullPointerException {
		return Check.notNull(get(k));
	}

	@Override
	public DataArray getArray(K k) throws IllegalArgumentException{
		return (DataArray)get(k);
	}

	@Override
	public DataObject getObject(K k) throws IllegalArgumentException {
		return (DataObject)get(k);
	}

	@Override
	public void setPathProperty(String path, @Nullable Object value) {
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		explodePath(path).put(pathBits[len-1], value);
	}
	
	@Override
	public void setOrPushPathProperty(String path, @Nullable  Object value) {
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		explodePath(path).setOrPushProperty(pathBits[len-1], value);
	}
	
	@SuppressWarnings("unchecked")
	private PropertyBag<Object> explodePath(String path) {
		PropertyBag<Object> _target = asObjectKeyedDataStruct();
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
			_target = (PropertyBag<Object>)newTarget;
		}
		return _target;
	}

	private void setProperty(Object key, @Nullable Object value) {
		asObjectKeyedDataStruct().put(key, value);
	}
	
	@Override
	public void setOrPushProperty(Object key, @Nullable Object value) {
		PropertyBag<Object> _this = asObjectKeyedDataStruct();
		if(_this.exists(key)) {
			Object cur = _this.get(key);
			if(STRUCT.isDataArray(cur)) {
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
	private PropertyBag<Object> asObjectKeyedDataStruct() {
		return (PropertyBag<Object>)this;
	}
	private Object toStringOrIntKey(String s) {
		Check.notNull(s);
		return Maths.isInteger(s) ? Integer.valueOf(s) : s;
	}
	@SuppressWarnings("unchecked")
	private PropertyBag<Object> asObjectKeyedDataStruct(DataStruct ds) {
		return (PropertyBag<Object>)ds;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getPathSafe(String... pathBits) {
		PropertyBag<Object> _target = asObjectKeyedDataStruct();
		int len = pathBits.length;
		for(int i = 0; i < len; i++) {
			String pathBit = pathBits[i];
			Object k = toStringOrIntKey(pathBit);
			Object newTarget = _target.get(k);
			if(isPrimitiveOrNull(newTarget)) {
				return i == len - 1 ? newTarget : null;
			} else {
				_target = (PropertyBag<Object>)newTarget;
			}
		}
		return _target;
	}
	
	@Override
	public Object getPathSafe(String path) {
		return getPathSafe(path.split("\\."));
	}
	
	@Override
	public String getString(K key) throws ClassCastException {
		return (String)get(key);
	}

	@Override
	public Number getNumber(K key) throws ClassCastException {
		return (Number)get(key);
	}

	@Override
	public Boolean getBoolean(K key) throws ClassCastException {
		return (Boolean)get(key);
	}

	@Override
	public String getPathSafeString(String path) throws ClassCastException {
		return (String)getPathSafe(path);
	}
	
	@Override
	public String getPathSafeString(String... pathBits) throws ClassCastException {
		return (String)getPathSafe(pathBits);
	}

	@Override
	public Number getPathSafeNumber(String path) throws ClassCastException {
		return (Number)getPathSafe(path);
	}
	
	@Override
	public Number getPathSafeNumber(String... pathBits) throws ClassCastException {
		return (Number)getPathSafe(pathBits);
	}

	@Override
	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		return (Boolean)getPathSafe(path);
	}
	
	@Override
	public Boolean getPathSafeBoolean(String... pathBits) throws ClassCastException {
		return (Boolean)getPathSafe(pathBits);
	}

	@Override
	public DataObject getPathSafeObject(String path) throws ClassCastException {
		return (DataObject)getPathSafe(path);
	}
	
	@Override
	public DataObject getPathSafeObject(String... pathBits) throws ClassCastException {
		return (DataObject)getPathSafe(pathBits);
	}

	@Override
	public DataArray getPathSafeArray(String path) throws ClassCastException {
		return (DataArray)getPathSafe(path);
	}
	
	@Override
	public DataArray getPathSafeArray(String... pathBits) throws ClassCastException {
		return (DataArray)getPathSafe(pathBits);
	}

	@Override
	public Long getLong(K key) throws ClassCastException {
		Number n = getNumber(key);
		return n == null ? null : n.longValue();
	}

	@Override
	public Double getDouble(K key) throws ClassCastException {
		Number n = getNumber(key);
		return n == null ? null : n.doubleValue();
	}

	@Override
	public String getStrictString(K key) throws ClassCastException, NullPointerException {
		return Check.notNull(getString(key));
	}

	@Override
	public Long getStrictLong(K key) throws ClassCastException, NullPointerException {
		return Check.notNull(getLong(key));
	}

	@Override
	public Double getStrictDouble(K key) throws ClassCastException, NullPointerException {
		return Check.notNull(getDouble(key));
	}

	@Override
	public Boolean getStrictBoolean(K key) throws ClassCastException, NullPointerException {
		return Check.notNull(getBoolean(key));
	}

	@Override
	public DataObject getStrictObject(K key) throws ClassCastException, NullPointerException {
		return Check.notNull(getObject(key));
	}

	@Override
	public DataArray getStrictArray(K key) throws ClassCastException, NullPointerException {
		return Check.notNull(getArray(key));
	}

	@Override
	public Long putLong(K key, Long o) {
		return (Long)put(key, o);
	}

	@Override
	public Double putDouble(K key, Double o) {
		return (Double)put(key, o);
	}

	@Override
	public String putString(K key, String o) {
		return (String)put(key, o);
	}

	@Override
	public Boolean putBoolean(K key, Boolean o) {
		return (Boolean)put(key, o);
	}

	@Override
	public DataObject putObject(K key, DataObject o) {
		return (DataObject)put(key, o);
	}

	@Override
	public DataArray putArray(K key, DataArray o) {
		return (DataArray)put(key, o);
	}

	protected void onMutate() {
		json = null;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return STRUCT.clone(this);
	}

	@Override
	public String toString() {
		return json != null ? json : STRUCT.toJsonString(this);
	}

	@Override
	public boolean equals(Object obj) {
		return STRUCT.equals(this, obj);
	}

	@Override
	public int hashCode() {
		if (json == null) {
			json = toString();
		}
		return json.hashCode();
	}
}
