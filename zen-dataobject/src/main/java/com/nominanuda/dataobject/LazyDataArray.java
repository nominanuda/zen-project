package com.nominanuda.dataobject;

import java.util.Iterator;
import java.util.List;


public class LazyDataArray implements DataArray {
	private DataArray delegee;
	private final String serialized;
	
	public LazyDataArray(String serialized) {
		this.serialized = serialized;
	}

	public boolean isExploded() {
		return delegee != null;
	}

	public void explode() {
		if(delegee == null) {
			try {
				delegee = (DataArray)new JSONParser().parse(serialized);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public String toString() {
		return serialized;
	}

	public Object add(Object val) {
		explode();
		return delegee.add(val);
	}

	public DataObject addNewObject() {
		explode();
		return delegee.addNewObject();
	}

	public DataArray addNewArray() {
		explode();
		return delegee.addNewArray();
	}

	public int getLength() {
		explode();
		return delegee.getLength();
	}
	
	@Override
	public boolean isEmpty() {
		return getLength() == 0;
	}

	public Object get(Integer key) {
		explode();
		return delegee.get(key);
	}

//	@SuppressWarnings("deprecation")
//	public DataStruct getParent() {
//		explode();
//		return delegee.getParent();
//	}

	public Object getStrict(Integer key) throws NullPointerException {
		explode();
		return delegee.getStrict(key);
	}

//	@SuppressWarnings("deprecation")
//	public DataStruct getRoot() {
//		explode();
//		return delegee.getRoot();
//	}

	public boolean isArray() {
		explode();
		return delegee.isArray();
	}

	public boolean isObject() {
		explode();
		return delegee.isObject();
	}

	public boolean exists(Integer k) {
		explode();
		return delegee.exists(k);
	}

	public String getType() {
		explode();
		return delegee.getType();
	}

	public DataArray asArray() throws ClassCastException {
		explode();
		return delegee.asArray();
	}

	public List<Integer> getKeys() {
		explode();
		return delegee.getKeys();
	}

	public DataObject asObject() throws ClassCastException {
		explode();
		return delegee.asObject();
	}

	public boolean isPrimitiveOrNull(Object o) {
		explode();
		return DataStructHelper.STRUCT.isPrimitiveOrNull(o);
	}

	public Object getPathSafe(String path) {
		explode();
		return delegee.getPathSafe(path);
	}

	public String getString(Integer key) throws ClassCastException {
		explode();
		return delegee.getString(key);
	}

	public Number getNumber(Integer key) throws ClassCastException {
		explode();
		return delegee.getNumber(key);
	}

	public Boolean getBoolean(Integer key) throws ClassCastException {
		explode();
		return delegee.getBoolean(key);
	}

	public DataObject getObject(Integer key) throws ClassCastException {
		explode();
		return delegee.getObject(key);
	}

	public DataArray getArray(Integer key) throws ClassCastException {
		explode();
		return delegee.getArray(key);
	}

	public String getPathSafeString(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeString(path);
	}

	public Number getPathSafeNumber(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeNumber(path);
	}

	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeBoolean(path);
	}

	public DataObject getPathSafeObject(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeObject(path);
	}

	public DataArray getPathSafeArray(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeArray(path);
	}

	public Long getLong(Integer key) throws ClassCastException {
		explode();
		return delegee.getLong(key);
	}

	public Double getDouble(Integer key) throws ClassCastException {
		explode();
		return delegee.getDouble(key);
	}

	public String getStrictString(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictString(key);
	}

	public Long getStrictLong(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictLong(key);
	}

	public Double getStrictDouble(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictDouble(key);
	}

	public Boolean getStrictBoolean(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictBoolean(key);
	}

	public DataObject getStrictObject(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictObject(key);
	}

	public DataArray getStrictArray(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictArray(key);
	}

	public Iterator<Object> iterator() {
		explode();
		return delegee.iterator();
	}

	public Iterator<Integer> keyIterator() {
		explode();
		return delegee.keyIterator();
	}

	public Object put(Integer key, Object o) {
		explode();
		return delegee.put(key, o);
	}

	public Object remove(Integer key) {
		explode();
		return delegee.remove(key);
	}

	public DataArray putNewArray(Integer key) {
		explode();
		return delegee.putNewArray(key);
	}

	public DataObject putNewObject(Integer key) {
		explode();
		return delegee.putNewObject(key);
	}

	public void setPathProperty(String path, Object value) {
		explode();
		delegee.setPathProperty(path, value);
	}

	public void setOrPushPathProperty(String path, Object value) {
		explode();
		delegee.setOrPushPathProperty(path, value);
	}

	public void setOrPushProperty(Object key, Object value) {
		explode();
		delegee.setOrPushProperty(key, value);
	}

	public Long putLong(Integer key, Long o) {
		explode();
		return delegee.putLong(key, o);
	}

	public Double putDouble(Integer key, Double o) {
		explode();
		return delegee.putDouble(key, o);
	}

	public String putString(Integer key, String o) {
		explode();
		return delegee.putString(key, o);
	}

	public Boolean putBoolean(Integer key, Boolean o) {
		explode();
		return delegee.putBoolean(key, o);
	}

	public DataObject putObject(Integer key, DataObject o) {
		explode();
		return delegee.putObject(key, o);
	}

	public DataArray putArray(Integer key, DataArray o) {
		explode();
		return delegee.putArray(key, o);
	}

	public DataArray with(Object val) {
		add(val);
		return this;
	}

}
