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

	@Override
	public String toString() {
		return serialized;
	}

	@Override
	public boolean add(Object val) {
		explode();
		return delegee.add(val);
	}

	@Override
	public DataObject addNewObject() {
		explode();
		return delegee.addNewObject();
	}

	@Override
	public DataArray addNewArray() {
		explode();
		return delegee.addNewArray();
	}

	@Override
	public int getLength() {
		explode();
		return delegee.getLength();
	}
	
	@Override
	public boolean isEmpty() {
		return getLength() == 0;
	}

	@Override
	public Object get(Integer key) {
		explode();
		return delegee.get(key);
	}

	@Override
	public Object getStrict(Integer key) throws NullPointerException {
		explode();
		return delegee.getStrict(key);
	}

	@Override
	public boolean isArray() {
		explode();
		return delegee.isArray();
	}

	@Override
	public boolean isObject() {
		explode();
		return delegee.isObject();
	}

	@Override
	public boolean exists(Integer k) {
		explode();
		return delegee.exists(k);
	}

	@Override
	public String getType() {
		explode();
		return delegee.getType();
	}

	@Override
	public DataArray asArray() throws ClassCastException {
		explode();
		return delegee.asArray();
	}

	@Override
	public List<Integer> getKeys() {
		explode();
		return delegee.getKeys();
	}

	@Override
	public DataObject asObject() throws ClassCastException {
		explode();
		return delegee.asObject();
	}

	@Override
	public boolean isPrimitiveOrNull(Object o) {
		explode();
		return DataStructHelper.STRUCT.isPrimitiveOrNull(o);
	}

	@Override
	public Object getPathSafe(String path) {
		explode();
		return delegee.getPathSafe(path);
	}
	
	@Override
	public Object getPathSafe(String... pathBits) {
		explode();
		return delegee.getPathSafe(pathBits);
	}

	@Override
	public String getString(Integer key) throws ClassCastException {
		explode();
		return delegee.getString(key);
	}

	@Override
	public Number getNumber(Integer key) throws ClassCastException {
		explode();
		return delegee.getNumber(key);
	}

	@Override
	public Boolean getBoolean(Integer key) throws ClassCastException {
		explode();
		return delegee.getBoolean(key);
	}

	@Override
	public DataObject getObject(Integer key) throws ClassCastException {
		explode();
		return delegee.getObject(key);
	}

	@Override
	public DataArray getArray(Integer key) throws ClassCastException {
		explode();
		return delegee.getArray(key);
	}

	@Override
	public String getPathSafeString(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeString(path);
	}
	
	@Override
	public String getPathSafeString(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeString(pathBits);
	}

	@Override
	public Number getPathSafeNumber(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeNumber(path);
	}

	@Override
	public Number getPathSafeNumber(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeNumber(pathBits);
	}

	@Override
	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeBoolean(path);
	}

	@Override
	public Boolean getPathSafeBoolean(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeBoolean(pathBits);
	}

	@Override
	public DataObject getPathSafeObject(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeObject(path);
	}

	@Override
	public DataObject getPathSafeObject(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeObject(pathBits);
	}

	@Override
	public DataArray getPathSafeArray(String path) throws ClassCastException {
		explode();
		return delegee.getPathSafeArray(path);
	}
	
	@Override
	public DataArray getPathSafeArray(String... pathBits) throws ClassCastException {
		explode();
		return delegee.getPathSafeArray(pathBits);
	}

	@Override
	public Long getLong(Integer key) throws ClassCastException {
		explode();
		return delegee.getLong(key);
	}

	@Override
	public Double getDouble(Integer key) throws ClassCastException {
		explode();
		return delegee.getDouble(key);
	}

	@Override
	public String getStrictString(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictString(key);
	}

	@Override
	public Long getStrictLong(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictLong(key);
	}

	@Override
	public Double getStrictDouble(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictDouble(key);
	}

	@Override
	public Boolean getStrictBoolean(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictBoolean(key);
	}

	@Override
	public DataObject getStrictObject(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictObject(key);
	}

	@Override
	public DataArray getStrictArray(Integer key) throws ClassCastException,
			NullPointerException {
		explode();
		return delegee.getStrictArray(key);
	}

	@Override
	public Iterator<Object> iterator() {
		explode();
		return delegee.iterator();
	}

	@Override
	public Iterator<Integer> keyIterator() {
		explode();
		return delegee.keyIterator();
	}

	@Override
	public Object put(Integer key, Object o) {
		explode();
		return delegee.put(key, o);
	}

	@Override
	public Object remove(int key) {
		explode();
		return delegee.remove(key);
	}

	@Override
	public DataArray putNewArray(Integer key) {
		explode();
		return delegee.putNewArray(key);
	}

	@Override
	public DataObject putNewObject(Integer key) {
		explode();
		return delegee.putNewObject(key);
	}

	@Override
	public void setPathProperty(String path, Object value) {
		explode();
		delegee.setPathProperty(path, value);
	}

	@Override
	public void setOrPushPathProperty(String path, Object value) {
		explode();
		delegee.setOrPushPathProperty(path, value);
	}

	@Override
	public void setOrPushProperty(Object key, Object value) {
		explode();
		delegee.setOrPushProperty(key, value);
	}

	@Override
	public Long putLong(Integer key, Long o) {
		explode();
		return delegee.putLong(key, o);
	}

	@Override
	public Double putDouble(Integer key, Double o) {
		explode();
		return delegee.putDouble(key, o);
	}

	@Override
	public String putString(Integer key, String o) {
		explode();
		return delegee.putString(key, o);
	}

	@Override
	public Boolean putBoolean(Integer key, Boolean o) {
		explode();
		return delegee.putBoolean(key, o);
	}

	@Override
	public DataObject putObject(Integer key, DataObject o) {
		explode();
		return delegee.putObject(key, o);
	}

	@Override
	public DataArray putArray(Integer key, DataArray o) {
		explode();
		return delegee.putArray(key, o);
	}

	@Override
	public DataArray with(Object val) {
		add(val);
		return this;
	}

}
