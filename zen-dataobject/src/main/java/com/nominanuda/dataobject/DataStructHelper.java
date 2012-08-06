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

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Maths;
import com.nominanuda.lang.SafeConvertor;
import com.nominanuda.lang.SetList;

import static com.nominanuda.dataobject.DataType.*;

@ThreadSafe
public class DataStructHelper implements Serializable, DataStructFactory {
	private static final long serialVersionUID = -4825883006001134937L;
	public static final int MERGE_POLICY_OVERRIDE = 0;
	public static final int MERGE_POLICY_PUSH = 1;
	private static final String MULTIVALUE_SUFFIX = "[]";
	private static final int MULTIVALUE_SUFFIX_LEN = MULTIVALUE_SUFFIX.length();

	public boolean isPrimitiveOrNull(Object o) {
		return o == null || o instanceof Boolean || o instanceof Number
				|| o instanceof String;
	}

	public DataType getDataType(Object o) {
		return o == null ? nil : o instanceof Boolean ? bool
				: o instanceof Number ? number : o instanceof String ? string
						: o instanceof DataArray ? array
								: o instanceof DataObject ? object
										: (DataType) Check.illegalargument
												.fail();
	}

	public boolean isDataStruct(Object o) {
		return o != null && o instanceof DataStruct;
	}

	public boolean isDataObject(Object o) {
		return o != null && o instanceof DataObject;
	}

	public boolean isDataArray(Object o) {
		return o != null && o instanceof DataArray;
	}

	public @Nullable
	String toJsonStringValsNoNulls(Object o) {
		if (o == null) {
			return null;
		} else if (isDataObject(o)) {
			DataObject obj = (DataObject) o;
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			Iterator<String> itr = obj.getKeys().iterator();
			while (itr.hasNext()) {
				String k = itr.next();
				String s = toJsonStringValsNoNulls(obj.get(k));
				if (s != null) {
					sb.append("\"" + k + "\":" + s);
					if (itr.hasNext()) {
						sb.append(",");
					}
				}
			}
			sb.append("}");
			return sb.toString();
		} else if (isDataArray(o)) {
			DataArray arr = (DataArray) o;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			int len = arr.getLength();
			for (int i = 0; i < len; i++) {
				String s = toJsonStringValsNoNulls(arr.get(i));
				if (s != null) {
					sb.append(s);
					if (i < len - 1) {
						sb.append(",");
					}
				}
			}
			sb.append("]");
			return sb.toString();
		} else if (o instanceof Number) {
			Number n = (Number) o;
			if (Maths.isInteger(n.doubleValue())) {
				return "\"" + new Long(n.longValue()).toString() + "\"";
			} else {
				return "\"" + n.toString() + "\"";
			}
		} else if (o instanceof String) {
			return "\""
					+ ((String) o).replace("\\", "\\\\").replace("\"", "\\\"")
					+ "\"";
		} else if (o instanceof Boolean) {
			return "\"" + ((Boolean) o).toString() + "\"";
		} else {
			throw new IllegalArgumentException(
					"cannot convert to string an object of type:"
							+ o.getClass().getName());
		}
	}

	public String toJsonString(Object o) {
		if (o == null) {
			return "null";
		} else if (isDataObject(o)) {
			StringWriter sw = new StringWriter();
			JsonPrinter p = new JsonPrinter(sw, false);
			DataStructStreamer.stream((DataObject) o, p);
			return sw.toString();
		} else if (isDataArray(o)) {
			StringWriter sw = new StringWriter();
			JsonPrinter p = new JsonPrinter(sw, false);
			DataStructStreamer.stream((DataArray) o, p);
			return sw.toString();
		} else if (o instanceof Number) {
			Number n = (Number) o;
			if (Maths.isInteger(n.doubleValue())) {
				return new Long(n.longValue()).toString();
			} else {
				return n.toString();
			}
		} else if (o instanceof String) {
			return "\"" + jsonStringEscape((String) o) + "\"";
		} else if (o instanceof Boolean) {
			return ((Boolean) o).toString();
		} else {
			throw new IllegalArgumentException(
					"cannot convert to string an object of type:"
							+ o.getClass().getName());
		}
	}

	public String jsonStringEscape(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"")
				.replace("\n", "\\n");
	}

	public String jsonStringUnescape(String s) {
		return s.replace("\\\\", "\\").replace("\\\"", "\"")
				.replace("\\n", "\\n");
	}

	public void copy(DataObject src, DataObject dst, int policy)
			throws UnsupportedOperationException {
		switch (policy) {
		case MERGE_POLICY_OVERRIDE:
			copyOverwite(src, dst);
			break;
		case MERGE_POLICY_PUSH:
			copyPush(src, dst);
			break;
		default:
			throw new UnsupportedOperationException("unknown merge policy:"
					+ policy);
		}
	}

	public <K, T extends DataStruct> void copyOverwite(PropertyBag<K> source,
			PropertyBag<K> target) {
		Iterator<K> itr = source.keyIterator();
		while (itr.hasNext()) {
			K key = itr.next();
			Object o = source.get(key);
			if (isPrimitiveOrNull(o)) {
				target.put(key, o);
			} else if (isDataArray(o)) {
				copyOverwite((DataArray) o, target.putNewArray(key));
			} else if (isDataObject(o)) {
				copyOverwite((DataObject) o, target.putNewObject(key));
			} else {
				throw new IllegalStateException(
						o.getClass().getName()
								+ " is neither a DataStruct nor a primitive type or null");
			}
		}
	}

	/**
	 * {a:1} {b:1} => {a:1, b:1} {a:1} {a:1} => {a:[1, 1]} {a:1} {a:null} =>
	 * {a:[1, null]} {a:{b:1}} {a:null} => {a:[{b:1}, null]} {a:{b:1}}
	 * {a:{b:{c:null}}} => {a:{b:[1,{c:null}]}} {a:1} {a:[2]} => {a:[1,2]}
	 * {a:[1]} {a:[2]} => {a:[1,2]} {a:{b:1}} {a:[2]} => {a:[{b:1},2]} {a:{b:1}}
	 * {a:{b:[2]}} => {a:{b:[1,2]}} {a:1} {} => {a:1}}
	 */
	public void copyPush(DataObject source, DataObject target) {
		Iterator<String> itr = source.keyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Object sval = source.get(key);
			putPush(target, key, sval);
		}
	}

	public void putPush(DataObject target, String key, Object val) {
		if (target.exists(key)) {
			Object tval = target.get(key);
			if (isDataObject(val) && isDataObject(tval)) {
				copyPush((DataObject) val, (DataObject) tval);
			} else if (isDataArray(tval)) {
				((DataArray) tval).add(val);
			} else {
				DataArray a = target.putNewArray(key);
				a.add(tval);
				a.add(val);
			}
		} else {
			target.put(key, val);
		}
	}

	public String primitiveOrNullToString(@Nullable Object o) {
		Check.illegalargument.assertTrue(isPrimitiveOrNull(o));
		return o == null ? "null" : o instanceof Number ? Maths
				.toString((Number) o) : o.toString();
	}

	@SuppressWarnings("unchecked")
	public DataArray fromMapsAndCollections(@SuppressWarnings("rawtypes") Collection c) {
		DataArrayImpl a = new DataArrayImpl();
		deepCopy(c, a);
		return a;
	}

	public DataObject fromMapsAndCollections(Map<String, Object> m) {
		DataObjectImpl o = new DataObjectImpl();
		deepCopy(m, o);
		return o;
	}

	@SuppressWarnings("unchecked")
	private void deepCopy(Map<String, Object> m, DataObject o) {
		for (Entry<String, Object> e : m.entrySet()) {
			Object v = e.getValue();
			if (v == null) {
				o.put(e.getKey(), null);
			} else if (v instanceof Map<?, ?>) {
				DataObject tobj = o.putNewObject(e.getKey());
				deepCopy((Map<String, Object>) v, tobj);
			} else if (v instanceof Collection<?>) {
				DataArray tarr = o.putNewArray(e.getKey());
				deepCopy((Collection<Object>) v, tarr);
			} else {
				o.put(e.getKey(), v);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void deepCopy(Collection<Object> c, DataArray tarr) {
		for (Object v : c) {
			if (v == null) {
				tarr.add(null);
			} else if (v instanceof Map<?, ?>) {
				DataObject tobj = tarr.addNewObject();
				deepCopy((Map<String, Object>) v, tobj);
			} else if (v instanceof Collection<?>) {
				DataArray a = tarr.addNewArray();
				deepCopy((Collection<Object>) v, a);
			} else {
				tarr.add(v);
			}
		}
	}

	public boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1 == o2) {
			return true;
		}
		if (!o1.getClass().equals(o2.getClass())) {
			return false;
		}
		if (o1 instanceof DataStruct) {
			@SuppressWarnings("unchecked")
			PropertyBag<Object> ds1 = (PropertyBag<Object>) o1;
			@SuppressWarnings("unchecked")
			PropertyBag<Object> ds2 = (PropertyBag<Object>) o2;
			Iterator<Object> k1Itr = ds1.keyIterator();
			Set<Object> analyzedKeys = new HashSet<Object>();
			while (k1Itr.hasNext()) {
				Object k1 = k1Itr.next();
				Object v1 = ds1.get(k1);
				Object v2 = ds2.get(k1);
				if (!equals(v1, v2)) {
					return false;
				}
				analyzedKeys.add(k1);
			}
			Iterator<Object> k2Itr = ds2.keyIterator();
			while (k2Itr.hasNext()) {
				if (!analyzedKeys.contains(k2Itr.next())) {
					return false;
				}
			}
			return true;
		} else {
			Check.illegalstate.assertTrue(isPrimitiveOrNull(o1)
					&& isPrimitiveOrNull(o1));
			return o1.equals(o2);
		}
	}

	public <K, T extends PropertyBag<K>> T clone(T struct) {
		Check.notNull(struct);
		Iterator<K> keyItr = struct.keyIterator();
		@SuppressWarnings("unchecked")
		T target = (T) (struct instanceof DataArray ? new DataArrayImpl()
				: new DataObjectImpl());
		while (keyItr.hasNext()) {
			K key = keyItr.next();
			Object val = struct.get(key);
			if (isPrimitiveOrNull(val)) {
				target.put(key, val);
			} else if (val instanceof DataArray) {
				target.put(key, clone((DataArray) val));
			} else {
				target.put(key, clone((DataObject) val));
			}
		}
		return target;
	}
	public <K, T extends PropertyBag<K>> T clone(T struct, AbstractDataStruct<K> parent) {
		Check.notNull(struct);
		Iterator<K> keyItr = struct.keyIterator();
		@SuppressWarnings("unchecked")
		T target = (T) (struct instanceof DataArray ? new DataArrayImpl(parent)
				: new DataObjectImpl(parent));
		while (keyItr.hasNext()) {
			K key = keyItr.next();
			Object val = struct.get(key);
			if (isPrimitiveOrNull(val)) {
				target.put(key, val);
			} else if (val instanceof DataArray) {
				target.put(key, clone((DataArray) val));
			} else {
				target.put(key, clone((DataObject) val));
			}
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	public <T> Iterable<T> castAsIterable(DataArray arr) {
		return (Iterable<T>) arr;
	}

	public void toFlatMap(DataStruct from, Map<String, Object> map) {
		toFlatMap(map, "", from);
	}

	private void toFlatMap(Map<String, Object> map, String key,
			@Nullable Object value) {
		switch (getDataType(value)) {
		case object:
			DataObject obj = (DataObject) value;
			for (String k : obj.getKeys()) {
				Object val = obj.getStrict(k);
				toFlatMap(map, keyJoin(key, k), val);
			}
			break;
		case array:
			DataArray arr = ((DataArray) value);
			int len = arr.getLength();
			for (int i = 0; i < len; i++) {
				toFlatMap(map, keyJoin(key, i), arr.get(i));
			}
			break;
		case nil:
		case number:
		case bool:
		case string:
			map.put(key, value);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private String keyJoin(String prefix, Object suffix) {
		Check.illegalstate.assertTrue(suffix instanceof Integer
				|| suffix instanceof String);
		return (prefix.length() > 0) ? prefix + "." + suffix.toString()
				: suffix.toString();
	}

	private void writeFlatMapTo(Map<String, Object> pMap, DataObject dest) {
		for (Entry<String, ?> e : pMap.entrySet()) {
			writeScalarOrMultivaluedProperty(dest, e.getKey(), e.getValue());
		}
	}

	public DataStruct fromFlatMap(Map<String, Object> map) {
		DataObject dest = new DataObjectImpl();
		writeFlatMapTo(map, dest);
		return dest;
	}

	private void writeScalarOrMultivaluedProperty(DataObject target,
			String path, Object val) {
		if (val.getClass().isArray()) {
			val = Arrays.asList((Object[]) val);
		}
		if (!(val == null || val instanceof Collection<?>)) {// scalar
			if (path.endsWith(MULTIVALUE_SUFFIX)) {
				path = path.substring(0, path.length() - MULTIVALUE_SUFFIX_LEN)
						+ ".0";
			}
			writeScalarProperty(target, path, val);
		} else {// Collection
			Collection<?> l = (Collection<?>) val;
			int len = l.size();
			if (len == 0) {
				return;
				// writeScalarProperty(path, null);
			} else if (len == 1) {
				if (path.endsWith(MULTIVALUE_SUFFIX)) {
					path = path.substring(0, path.length()
							- MULTIVALUE_SUFFIX_LEN)
							+ ".0";
				}
				writeScalarProperty(target, path, l.iterator().next());
			} else {
				if (path.endsWith(MULTIVALUE_SUFFIX)) {
					path = path.substring(0, path.length()
							- MULTIVALUE_SUFFIX_LEN);
				}
				int i = 0;
				for (Object v : l) {
					writeScalarProperty(target, path + "." + i, v);
					i++;
				}
			}
		}

	}

	private void writeScalarProperty(DataObject target, String path, Object val) {
		if (val != null
				&& (val.getClass().isArray() || val instanceof Collection<?>)) {
			throw new IllegalArgumentException(val.getClass()
					+ " is not a scalar type");
		}
		// to allow alternative array syntax a[0] instead of a.0
		path = path.replaceAll("\\[(\\d+)\\]", "\\.$1");
		String[] bits = path.split("\\.");
		writeScalarProperty(target, bits, val);
	}

	private void writeScalarProperty(DataObject target, String[] path,
			Object val) {
		String k = path[0];
		if (path.length == 1) {
			target.put(k, val);
		} else {
			Object o = target.get(k);
			String[] subPath = new String[path.length - 1];// Arrays.copyOfRange(path,
															// 1, path.length);
			System.arraycopy(path, 1, subPath, 0, subPath.length);
			String nextKey = subPath[0];
			if (Maths.isInteger(nextKey)) {
				DataArray newTarget = isDataArray(o) ? (DataArray) o : target
						.putNewArray(k);
				writeScalarProperty(newTarget, subPath, val);
			} else {
				DataObject newTarget = isDataObject(o) ? (DataObject) o
						: target.putNewObject(k);
				writeScalarProperty(newTarget, subPath, val);
			}
		}
	}

	private void writeScalarProperty(DataArray target, String[] path, Object val) {
		Integer k = Integer.parseInt(path[0]);
		if (path.length == 1) {
			target.put(k, val);
		} else {
			Object o = target.getLength() > k ? target.get(k) : null;
			// String[] subPath = Arrays.copyOfRange(path, 1, path.length);
			String[] subPath = new String[path.length - 1];// Arrays.copyOfRange(path,
															// 1, path.length);
			System.arraycopy(path, 1, subPath, 0, subPath.length);
			String nextKey = subPath[0];
			if (Maths.isInteger(nextKey)) {
				DataArray newTarget = isDataArray(o) ? (DataArray) o : target
						.putNewArray(k);

				writeScalarProperty(newTarget, subPath, val);
			} else {
				DataObject newTarget = isDataObject(o) ? (DataObject) o
						: target.putNewObject(k);
				writeScalarProperty(newTarget, subPath, val);
			}
		}
	}

	public Map<String, ? super Object> toMapsAndSetLists(DataObject o) {
		Map<String,Object> res = new LinkedHashMap<String, Object>();
		for(String k : o.getKeys()) {
			Object v = o.get(k);
			Object vToPut = 
				v == null ? null
				: v instanceof DataObject ? toMapsAndSetLists((DataObject)v)
				: v instanceof DataArray ? toMapsAndSetLists((DataArray)v)
				: v;
			res.put(k, vToPut);
		}
		return res;
	}
	public SetList<? super Object> toMapsAndSetLists(DataArray arr) {
		SetList<? super Object> res = new SetList<Object>();
		for(Object v : arr) {
			Object vToPut = 
				v == null ? null
				: v instanceof DataObject ? toMapsAndSetLists((DataObject)v)
				: v instanceof DataArray ? toMapsAndSetLists((DataArray)v)
				: v;
			res.add(vToPut);
		}
		return res;
	}
	//if convertor#canConvert returns false value is not added to result
	@SuppressWarnings("unchecked")
	public <X extends DataStruct> X convertLeaves(X source, SafeConvertor<Object, Object> convertor) {
		return Check.notNull(source) instanceof DataObject
			? (X)convertLeavesInternal((DataObject)source, convertor)
			: (X)convertLeavesInternal((DataArray)source, convertor);
	}

	private DataObject convertLeavesInternal(DataObject source, SafeConvertor<Object, Object> convertor) {
		DataObject res = new DataObjectImpl();
		for(String k : source.getKeys()) {
			Object v = source.get(k);
			if(isPrimitiveOrNull(v) && convertor.canConvert(v)) {
				res.put(k, convertor.apply(v));
			} else if(v instanceof DataObject) {
				res.put(k, convertLeavesInternal((DataObject)v, convertor));
			} else {
				res.put(k, convertLeavesInternal((DataArray)v, convertor));
			}
		}
		return res;
	}

	private DataArray convertLeavesInternal(DataArray source,
			SafeConvertor<Object, Object> convertor) {
		DataArray res = new DataArrayImpl();
		int len = source.getLength();
		for(int i = 0; i < len; i++) {
			Object v = source.get(i);
			if(isPrimitiveOrNull(v) && convertor.canConvert(v)) {
				res.add(convertor.apply(v));
			} else if(v instanceof DataObject) {
				res.add(convertLeavesInternal((DataObject)v, convertor));
			} else {
				res.add(convertLeavesInternal((DataArray)v, convertor));
			}
		}
		return res;
	}

	public DataObject newObject() {
		return new DataObjectImpl();
	}

	public DataArray newArray() {
		return new DataArrayImpl();
	}

}