package com.nominanuda.store.mongo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bson.BSONObject;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataType;
import com.nominanuda.dataobject.PropertyBag;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Maths;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

public class BSONObjectAsDataObject implements DataObject {
	private final BSONObject bson;

	public BSONObjectAsDataObject(BSONObject bson) {
		this.bson = bson;
	}

	public Iterator<String> keyIterator() {
		return bson.keySet().iterator();
	}

	public Object get(String key) {
		Object o = bson.get(key);
		return wrapIfNecessary(o);
	}

	static Object wrapIfNecessary(Object o) {
		if(STRUCT.isPrimitiveOrNull(o)) {
			return o;
		} else if(o instanceof List) {
			return new BSONListAsDataArray((List<?>)o);
		} else {
			return new BSONObjectAsDataObject(
				Check.illegalstate.assertInstanceOf(o, BSONObject.class));
		}
	}

	public boolean exists(String k) {
		return bson.containsField(k);
	}

	public List<String> getKeys() {
		return new LinkedList<String>(bson.keySet());
	}

	public Object getPathSafe(String path) {
		return getPathSafe(this, path);
	}
	@SuppressWarnings("unchecked")
	static Object getPathSafe(Object o, String path) {
		PropertyBag<Object> _target = asObjectKeyedDataStruct(o);
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		for (int i = 0; i < len; i++) {
			String pathBit = pathBits[i];
			Object k = toStringOrIntKey(pathBit);
			Object newTarget = _target.get(k);
			if (STRUCT.isPrimitiveOrNull(newTarget)) {
				return i == len - 1 ? newTarget : null;
			} else {
				_target = (PropertyBag<Object>) newTarget;
			}
		}
		return _target;
	}
	@SuppressWarnings("unchecked")
	static PropertyBag<Object> asObjectKeyedDataStruct(Object o) {
		return (PropertyBag<Object>)o;
	}
	static Object toStringOrIntKey(String s) {
		Check.notNull(s);
		return Maths.isInteger(s) ? Integer.valueOf(s) : s;
	}
	public Object put(String key, Object o) {
		return Check.unsupportedoperation.fail();
	}
	public DataStruct getParent() {
		return Check.unsupportedoperation.fail();
	}

	public DataStruct getRoot() {
		return Check.unsupportedoperation.fail();
	}

	public boolean isArray() {
		return false;
	}

	public boolean isObject() {
		return true;
	}

	public String getType() {
		return DataType.object.name();
	}

	public DataArray asArray() throws ClassCastException {
		throw new ClassCastException();
	}

	public DataObject asObject() throws ClassCastException {
		return this;
	}

	public Object getStrict(String key) throws NullPointerException {
		return Check.notNull(get(key));
	}

	public Object remove(String key) {
		return Check.unsupportedoperation.fail();
	}

	public DataArray putNewArray(String key) {
		return Check.unsupportedoperation.fail();
	}

	public DataObject putNewObject(String key) {
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

	public String getString(String key) throws ClassCastException {
		return (String)get(key);
	}

	public Number getNumber(String key) throws ClassCastException {
		return (Number)get(key);
	}

	public Boolean getBoolean(String key) throws ClassCastException {
		return (Boolean)get(key);
	}

	public DataObject getObject(String key) throws ClassCastException {
		return (DataObject)get(key);
	}

	public DataArray getArray(String key) throws ClassCastException {
		return (DataArray)get(key);
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
		return (DataArray)getPathSafe(path);	}

	public Long getLong(String key) throws ClassCastException {
		return (Long)get(key);
	}

	public Double getDouble(String key) throws ClassCastException {
		return (Double)get(key);
	}

	public String getStrictString(String key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getString(key));
	}

	public Long getStrictLong(String key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getLong(key));
	}

	public Double getStrictDouble(String key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getDouble(key));
	}

	public Boolean getStrictBoolean(String key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getBoolean(key));
	}

	public DataObject getStrictObject(String key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getObject(key));
	}

	public DataArray getStrictArray(String key) throws ClassCastException,
			NullPointerException {
		return Check.notNull(getArray(key));
	}

	public Long putLong(String key, Long o) {
		return Check.unsupportedoperation.fail();
	}

	public Double putDouble(String key, Double o) {
		return Check.unsupportedoperation.fail();
	}

	public String putString(String key, String o) {
		return Check.unsupportedoperation.fail();
	}

	public Boolean putBoolean(String key, Boolean o) {
		return Check.unsupportedoperation.fail();
	}

	public DataObject putObject(String key, DataObject o) {
		return Check.unsupportedoperation.fail();
	}

	public DataArray putArray(String key, DataArray o) {
		return Check.unsupportedoperation.fail();
	}

	public DataObject with(String k, Object v) {
		return Check.unsupportedoperation.fail();
	}

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return DataObjectImpl.iteratorOf(this);
	}

}
