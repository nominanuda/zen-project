package com.nominanuda.dataobject;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


public class LazyDataObject implements DataObject {
	private DataObject delegee;
	private final String serialized;
	
	public LazyDataObject(String serialized) {
		this.serialized = serialized;
	}

	public boolean isExploded() {
		return delegee != null;
	}

	public void explode() {
		if(delegee == null) {
			try {
				delegee = (DataObject)new JSONParser().parse(serialized);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public String toString() {
		return serialized;
	}

	public DataObject with(String k, Object v) {
		explode();
		return delegee.with(k, v);
	}

	public Iterator<String> keyIterator() {
		explode();
		return delegee.keyIterator();
	}

	public Object get(String key) {
		explode();
		return delegee.get(key);
	}

//	@SuppressWarnings("deprecation")
//	public DataStruct getParent() {
//		explode();
//		return delegee.getParent();
//	}

	public Object getStrict(String key) throws NullPointerException {
		explode();
		return delegee.getStrict(key);
	}

//	@SuppressWarnings("deprecation")
//	public DataStruct getRoot() {
//		explode();
//		return delegee.getRoot();
//	}

	public Object put(String key, Object o) {
		explode();
		return delegee.put(key, o);
	}

	public boolean isArray() {
		explode();
		return delegee.isArray();
	}

	public Object remove(String key) {
		explode();
		return delegee.remove(key);
	}

	public boolean isObject() {
		explode();
		return delegee.isObject();
	}

	public boolean exists(String k) {
		explode();
		return delegee.exists(k);
	}

	public String getType() {
		explode();
		return delegee.getType();
	}

	public DataArray putNewArray(String key) {
		explode();
		return delegee.putNewArray(key);
	}

	public DataArray asArray() throws ClassCastException {
		explode();
		return delegee.asArray();
	}

	public DataObject putNewObject(String key) {
		explode();
		return delegee.putNewObject(key);
	}

	public List<String> getKeys() {
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

	public Object getPathSafe(String path) {
		explode();
		return delegee.getPathSafe(path);
	}
	
	public Object getPathSafe(String... pathBits) {
		explode();
		return delegee.getPathSafe(pathBits);
	}

	public String getString(String key) throws ClassCastException {
		explode();
		return delegee.getString(key);
	}

	public Number getNumber(String key) throws ClassCastException {
		explode();
		return delegee.getNumber(key);
	}

	public Boolean getBoolean(String key) throws ClassCastException {
		explode();
		return delegee.getBoolean(key);
	}

	public DataObject getObject(String key) throws ClassCastException {
		explode();
		return delegee.getObject(key);
	}

	public DataArray getArray(String key) throws ClassCastException {
		explode();
		return delegee.getArray(key);
	}

	public String getPathSafeString(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeString(path);
	}
	
	public String getPathSafeString(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeString(pathBits);
	}

	public Number getPathSafeNumber(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeNumber(path);
	}
	
	public Number getPathSafeNumber(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeNumber(pathBits);
	}

	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeBoolean(path);
	}
	
	public Boolean getPathSafeBoolean(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeBoolean(pathBits);
	}

	public DataObject getPathSafeObject(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeObject(path);
	}
	
	public DataObject getPathSafeObject(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeObject(pathBits);
	}

	public DataArray getPathSafeArray(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeArray(path);
	}
	
	public DataArray getPathSafeArray(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeArray(pathBits);
	}

	public Long getLong(String key) throws ClassCastException {
		explode();
		return delegee.getLong(key);
	}

	public Double getDouble(String key) throws ClassCastException {
		explode();
		return delegee.getDouble(key);
	}

	public String getStrictString(String key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictString(key);
	}

	public Long getStrictLong(String key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictLong(key);
	}

	public Double getStrictDouble(String key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictDouble(key);
	}

	public Boolean getStrictBoolean(String key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictBoolean(key);
	}

	public DataObject getStrictObject(String key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictObject(key);
	}

	public DataArray getStrictArray(String key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictArray(key);
	}

	public Long putLong(String key, Long o) {
		explode();
		return delegee.putLong(key, o);
	}

	public Double putDouble(String key, Double o) {
		explode();
		return delegee.putDouble(key, o);
	}

	public String putString(String key, String o) {
		explode();
		return delegee.putString(key, o);
	}

	public Boolean putBoolean(String key, Boolean o) {
		explode();
		return delegee.putBoolean(key, o);
	}

	public DataObject putObject(String key, DataObject o) {
		explode();
		return delegee.putObject(key, o);
	}

	public DataArray putArray(String key, DataArray o) {
		explode();
		return delegee.putArray(key, o);
	}

	public Iterator<Entry<String, Object>> iterator() {
		explode();
		return delegee.iterator();
	}
}
