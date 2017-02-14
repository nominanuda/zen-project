package com.nominanuda.store.mongo;

import static com.nominanuda.store.mongo.BSONObjectAsDataObject.wrapIfNecessary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataType;
import com.nominanuda.lang.Check;

public class BSONListAsDataArray implements DataArray {
	private final List<?> l;

	public BSONListAsDataArray(List<?> o) {
		this.l = o;
	}

	public List<Integer> getKeys() {
		int len = getLength();
		ArrayList<Integer> l = new ArrayList<Integer>(len);
		for(int i = 0; i < len; i++) {
			l.add(i);
		}
		return l;	
	}

	public Iterator<Integer> keyIterator() {
		return getKeys().iterator();
	}

	public Object get(Integer key) {
		return wrapIfNecessary(l.get(key));
	}

	public Object getStrict(Integer key) throws NullPointerException {
		return Check.notNull(get(key));
	}

	public Object put(Integer key, Object o) {
		return Check.unsupportedoperation.fail();
	}

	public Object remove(int key) {
		return Check.unsupportedoperation.fail();
	}

	public boolean exists(Integer k) {
		return get(k) != null;
	}

	public DataArray putNewArray(Integer key) {
		return Check.unsupportedoperation.fail();
	}

	public Iterator<Object> iterator() {
		final Iterator<?> itr = l.iterator();
		return new Iterator<Object>() {
			public void remove() {
				Check.unsupportedoperation.fail();
			}
			public Object next() {
				return wrapIfNecessary(itr.next());
			}
			public boolean hasNext() {
				return itr.hasNext();
			}
		};
	}

	public DataObject putNewObject(Integer key) {
		return Check.unsupportedoperation.fail();
	}

	public boolean isPrimitiveOrNull(Object o) {
		Check.unsupportedoperation.fail();
		return false;
	}

	public void setPathProperty(String path, Object value) {
		Check.unsupportedoperation.fail();
	}

	public void setOrPushPathProperty(String path, Object value) {
		Check.unsupportedoperation.fail();
	}

	public void setProperty(Object key, Object value) {
		Check.unsupportedoperation.fail();
	}

	public void setOrPushProperty(Object key, Object value) {
		Check.unsupportedoperation.fail();
	}

	public Object getPathSafe(String path) {
		return BSONObjectAsDataObject.getPathSafe(this, path);
	}
	
	public Object getPathSafe(String... path) {
		return BSONObjectAsDataObject.getPathSafe(this, path);
	}

	public String getString(Integer key) throws ClassCastException {
		return (String)get(key);
	}

	public Number getNumber(Integer key) throws ClassCastException {
		return (Number)get(key);
	}

	public Boolean getBoolean(Integer key) throws ClassCastException {
		return (Boolean)get(key);
	}

	public DataObject getObject(Integer key) throws ClassCastException {
		return (DataObject)get(key);
	}

	public DataArray getArray(Integer key) throws ClassCastException {
		return (DataArray)get(key);
	}

	public String getPathSafeString(String path) throws ClassCastException {
		return (String)getPathSafe(path);
	}

	public String getPathSafeString(String... pathBits) throws ClassCastException {
		return (String)getPathSafe(pathBits);
	}

	public Number getPathSafeNumber(String path) throws ClassCastException {
		return (Number)getPathSafe(path);
	}

	public Number getPathSafeNumber(String... pathBits) throws ClassCastException {
		return (Number)getPathSafe(pathBits);
	}

	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		return (Boolean)getPathSafe(path);
	}

	public Boolean getPathSafeBoolean(String... pathBits) throws ClassCastException {
		return (Boolean)getPathSafe(pathBits);
	}

	public DataObject getPathSafeObject(String path) throws ClassCastException {
		return (DataObject)getPathSafe(path);
	}

	public DataObject getPathSafeObject(String... pathBits) throws ClassCastException {
		return (DataObject)getPathSafe(pathBits);
	}

	public DataArray getPathSafeArray(String path) throws ClassCastException {
		return (DataArray)getPathSafe(path);
	}
	
	public DataArray getPathSafeArray(String... pathBits) throws ClassCastException {
		return (DataArray)getPathSafe(pathBits);
	}

	public Long getLong(Integer key) throws ClassCastException {
		return (Long)get(key);
	}

	public Double getDouble(Integer key) throws ClassCastException {
		return (Double)get(key);
	}

	public String getStrictString(Integer key) throws ClassCastException,
			NullPointerException {
		return (String)getStrict(key);
	}

	public Long getStrictLong(Integer key) throws ClassCastException,
			NullPointerException {
		return (Long)getStrict(key);
	}

	public Double getStrictDouble(Integer key) throws ClassCastException,
			NullPointerException {
		return (Double)getStrict(key);
	}

	public Boolean getStrictBoolean(Integer key) throws ClassCastException,
			NullPointerException {
		return (Boolean)getStrict(key);
	}

	public DataObject getStrictObject(Integer key) throws ClassCastException,
			NullPointerException {
		return (DataObject)getStrict(key);
	}

	public DataArray getStrictArray(Integer key) throws ClassCastException,
			NullPointerException {
		return (DataArray)getStrict(key);
	}

	public Long putLong(Integer key, Long o) {
		return Check.unsupportedoperation.fail();
	}

	public Double putDouble(Integer key, Double o) {
		return Check.unsupportedoperation.fail();
	}

	public String putString(Integer key, String o) {
		return Check.unsupportedoperation.fail();
	}

	public Boolean putBoolean(Integer key, Boolean o) {
		return Check.unsupportedoperation.fail();
	}

	public DataObject putObject(Integer key, DataObject o) {
		return Check.unsupportedoperation.fail();
	}

	public DataArray putArray(Integer key, DataArray o) {
		return Check.unsupportedoperation.fail();
	}

	public DataStruct getParent() {
		return Check.unsupportedoperation.fail();
	}

	public DataStruct getRoot() {
		return Check.unsupportedoperation.fail();
	}

	public boolean isArray() {
		return true;
	}

	public boolean isObject() {
		return false;
	}

	public String getType() {
		return DataType.array.name();
	}

	public DataArray asArray() throws ClassCastException {
		return this;
	}

	public DataObject asObject() throws ClassCastException {
		throw new ClassCastException();
	}

	public boolean add(Object val) {
		return Check.unsupportedoperation.fail();
	}

	public DataObject addNewObject() {
		return Check.unsupportedoperation.fail();
	}

	public DataArray addNewArray() {
		return Check.unsupportedoperation.fail();
	}

	public int getLength() {
		return l.size();
	}
	
	@Override
	public boolean isEmpty() {
		return l.isEmpty();
	}

	public DataArray with(Object val) {
		add(val);
		return this;
	}

}
