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

import static com.nominanuda.dataobject.DataType.array;
import static com.nominanuda.dataobject.DataType.bool;
import static com.nominanuda.dataobject.DataType.nil;
import static com.nominanuda.dataobject.DataType.number;
import static com.nominanuda.dataobject.DataType.object;
import static com.nominanuda.dataobject.DataType.string;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.io.DevNull;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Collections;
import com.nominanuda.lang.Maths;
import com.nominanuda.lang.SafeConvertor;
import com.nominanuda.lang.SetList;
import com.nominanuda.lang.Strings;
import com.nominanuda.lang.Tuple2;

@ThreadSafe
public class DataStructHelper implements Serializable, DataStructFactory {
	public static final DataStructHelper STRUCT = new DataStructHelper();
	public static final DataStructHelper Z = new DataStructHelper();

	private static final long serialVersionUID = -4825883006001134937L;
	public static final int MERGE_POLICY_OVERRIDE = 0;
	public static final int MERGE_POLICY_PUSH = 1;
	private static final String MULTIVALUE_SUFFIX = "[]";
	private static final int MULTIVALUE_SUFFIX_LEN = MULTIVALUE_SUFFIX.length();

	public boolean isPrimitiveOrNull(Object o) {
		return o == null || o instanceof Boolean || o instanceof Number || o instanceof String;
	}

	public DataType getDataType(Object o) {
		return o == null ? nil
			: o instanceof Boolean ? bool
				: o instanceof Number ? number
					: o instanceof String ? string
						: o instanceof Arr ? array
								: o instanceof Obj ? object
									: (DataType) Check.illegalargument.fail();
	}

	public boolean isDataStruct(Object o) {
		return o != null && o instanceof DataStruct;
	}

	public boolean isDataObject(Object o) {
		return o != null && o instanceof Obj;
	}

	public boolean isDataArray(Object o) {
		return o != null && o instanceof Arr;
	}

	public @Nullable
	String toJsonStringValsNoNulls(Object o) {
		if (o == null) {
			return null;
		} else if (isDataObject(o)) {
			Obj obj = (Obj) o;
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
			Arr arr = (Arr) o;
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
			return "\"" + ((String) o).replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
		} else if (o instanceof Boolean) {
			return "\"" + ((Boolean) o).toString() + "\"";
		} else {
			throw new IllegalArgumentException("cannot convert to string an object of type:" + o.getClass().getName());
		}
	}

	private final JsonPrinter nullJsonPrinter = new JsonPrinter(DevNull.asWriter());

	public String toJsonString(Object o) {
		return toJsonString(o, false);
	}
	public String toJsonString(Object o, boolean pretty) {
		if (o == null) {
			return "null";
		} else if (isDataObject(o)) {
			StringWriter sw = new StringWriter();
			JsonPrinter p = new JsonPrinter(sw, pretty);
			DataStructStreamer.stream((Obj) o, p);
			return sw.toString();
		} else if (isDataArray(o)) {
			StringWriter sw = new StringWriter();
			JsonPrinter p = new JsonPrinter(sw, pretty);
			DataStructStreamer.stream((Arr) o, p);
			return sw.toString();
		} else if (o instanceof Number) {
			return nullJsonPrinter.numberEncode((Number)o);
		} else if (o instanceof String) {
			return "\"" + nullJsonPrinter.stringEncode((String) o) + "\"";
		} else if (o instanceof Boolean) {
			return ((Boolean) o).toString();
		} else {
			throw new IllegalArgumentException("cannot convert to string an object of type:" + o.getClass().getName());
		}
	}

	public String numberToString(Number n) {
		return nullJsonPrinter.numberEncode(n);
	}

	public String jsonStringEscape(String s) {
		return nullJsonPrinter.stringEncode(s);
	}

	ParserUtils parserUtils = new ParserUtils();
	public String jsonStringUnescape(String s) {
		return parserUtils.parseStringContent(s);
	}

	public void copy(Obj src, Obj dst, int policy) throws UnsupportedOperationException {
		switch (policy) {
		case MERGE_POLICY_OVERRIDE:
			copyOverwite(src, dst);
			break;
		case MERGE_POLICY_PUSH:
			copyPush(src, dst);
			break;
		default:
			throw new UnsupportedOperationException("unknown merge policy:" + policy);
		}
	}

	public <K> void copyOverwite(PropertyBag<K> source, PropertyBag<K> target) {
		Iterator<K> itr = source.keyIterator();
		while (itr.hasNext()) {
			K key = itr.next();
			Object o = source.get(key);
			if (isPrimitiveOrNull(o)) {
				target.put(key, o);
			} else if (isDataArray(o)) {
				copyOverwite((Arr) o, target.putNewArray(key));
			} else if (isDataObject(o)) {
				copyOverwite((Obj) o, target.putNewObject(key));
			} else {
				throw new IllegalStateException(o.getClass().getName() + " is neither a DataStruct nor a primitive type or null");
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
	public void copyPush(Obj source, Obj target) {
		Iterator<String> itr = source.keyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Object sval = source.get(key);
			putPush(target, key, sval);
		}
	}

	public void putPush(Obj target, String key, Object val) {
		if (target.exists(key)) {
			Object tval = target.get(key);
			if (isDataObject(val) && isDataObject(tval)) {
				copyPush((Obj) val, (Obj) tval);
			} else if (isDataArray(tval)) {
				((Arr) tval).add(val);
			} else {
				Arr a = target.putNewArray(key);
				a.add(tval);
				a.add(val);
			}
		} else {
			target.put(key, val);
		}
	}

	public String primitiveOrNullToString(@Nullable Object o) {
		Check.illegalargument.assertTrue(isPrimitiveOrNull(o));
		return o == null ? "null" : o instanceof Number ? Maths.toString((Number) o) : o.toString();
	}

	@SuppressWarnings("unchecked")
	public Arr fromMapsAndCollections(@SuppressWarnings("rawtypes") Collection c) {
		DataArrayImpl a = new DataArrayImpl();
		deepCopy(c, a);
		return a;
	}

	public Obj fromMapsAndCollections(Map<String, Object> m) {
		DataObjectImpl o = new DataObjectImpl();
		deepCopy(m, o);
		return o;
	}

	public Arr fromStringsCollection(Collection<String> list) {
		return fromMapsAndCollections(list);
	}
	
	public Obj fromStringsMap(Map<String, ?> map) {
		Obj obj = newObject();
		for (String key : map.keySet()) {
			obj.put(key, map.get(key));
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private void deepCopy(Map<String, Object> m, Obj o) {
		for (Entry<String, Object> e : m.entrySet()) {
			Object v = e.getValue();
			if (v == null) {
				o.put(e.getKey(), null);
			} else if (v instanceof Map<?, ?>) {
				Obj tobj = o.putNewObject(e.getKey());
				deepCopy((Map<String, Object>) v, tobj);
			} else if (v instanceof Collection<?>) {
				Arr tarr = o.putNewArray(e.getKey());
				deepCopy((Collection<Object>) v, tarr);
			} else {
				o.put(e.getKey(), v);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void deepCopy(Collection<Object> c, Arr tarr) {
		for (Object v : c) {
			if (v == null) {
				tarr.add(null);
			} else if (v instanceof Map<?, ?>) {
				Obj tobj = tarr.addNewObject();
				deepCopy((Map<String, Object>) v, tobj);
			} else if (v instanceof Collection<?>) {
				Arr a = tarr.addNewArray();
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
		T target = (T) (struct instanceof Arr ? new DataArrayImpl() : new DataObjectImpl());
		while (keyItr.hasNext()) {
			K key = keyItr.next();
			Object val = struct.get(key);
			if (isPrimitiveOrNull(val)) {
				target.put(key, val);
			} else if (val instanceof Arr) {
				target.put(key, clone((Arr) val));
			} else {
				target.put(key, clone((Obj) val));
			}
		}
		return target;
	}
	public <K, T extends PropertyBag<K>> T clone(T struct, AbstractDataStruct<K> parent) {
		Check.notNull(struct);
		Iterator<K> keyItr = struct.keyIterator();
		@SuppressWarnings("unchecked")
		T target = (T) (struct instanceof Arr ? new DataArrayImpl(parent) : new DataObjectImpl(parent));
		while (keyItr.hasNext()) {
			K key = keyItr.next();
			Object val = struct.get(key);
			if (isPrimitiveOrNull(val)) {
				target.put(key, val);
			} else if (val instanceof Arr) {
				target.put(key, clone((Arr) val));
			} else {
				target.put(key, clone((Obj) val));
			}
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	public <T> Iterable<T> castAsIterable(Arr arr) {
		return (Iterable<T>) arr;
	}

	public void toFlatMap(DataStruct from, Map<String, Object> map) {
		toFlatMap(map, "", from);
	}

	private void toFlatMap(Map<String, Object> map, String key, @Nullable Object value) {
		switch (getDataType(value)) {
		case object:
			Obj obj = (Obj) value;
			for (String k : obj.getKeys()) {
				Object val = obj.getStrict(k);
				toFlatMap(map, keyJoin(key, k), val);
			}
			break;
		case array:
			Arr arr = ((Arr) value);
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
		Check.illegalstate.assertTrue(suffix instanceof Integer || suffix instanceof String);
		return (prefix.length() > 0) ? prefix + "." + suffix.toString() : suffix.toString();
	}

	private void writeFlatMapTo(Map<String, Object> pMap, Obj dest) {
		for (Entry<String, ?> e : pMap.entrySet()) {
			writeScalarOrMultivaluedProperty(dest, e.getKey(), e.getValue());
		}
	}

	public DataStruct fromFlatMap(Map<String, Object> map) {
		Obj dest = new DataObjectImpl();
		writeFlatMapTo(map, dest);
		return dest;
	}

	private void writeScalarOrMultivaluedProperty(Obj target, String path, Object val) {
		if (val != null && val.getClass().isArray()) {
			val = Arrays.asList((Object[]) val);
		}
		if (!(val == null || val instanceof Collection<?>)) {// scalar
			if (path.endsWith(MULTIVALUE_SUFFIX)) {
				path = path.substring(0, path.length() - MULTIVALUE_SUFFIX_LEN) + ".0";
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
					path = path.substring(0, path.length() - MULTIVALUE_SUFFIX_LEN) + ".0";
				}
				writeScalarProperty(target, path, l.iterator().next());
			} else {
				if (path.endsWith(MULTIVALUE_SUFFIX)) {
					path = path.substring(0, path.length() - MULTIVALUE_SUFFIX_LEN);
				}
				int i = 0;
				for (Object v : l) {
					writeScalarProperty(target, path + "." + i, v);
					i++;
				}
			}
		}

	}

	private void writeScalarProperty(Obj target, String path, Object val) {
		if (val != null && (val.getClass().isArray() || val instanceof Collection<?>)) {
			throw new IllegalArgumentException(val.getClass() + " is not a scalar type");
		}
		// to allow alternative array syntax a[0] instead of a.0
		path = path.replaceAll("\\[(\\d+)\\]", "\\.$1");
		String[] bits = path.split("\\.");
		writeScalarProperty(target, bits, val);
	}

	private void writeScalarProperty(Obj target, String[] path, Object val) {
		String k = path[0];
		if (path.length == 1) {
			target.put(k, val);
		} else {
			Object o = target.get(k);
			String[] subPath = new String[path.length - 1];// Arrays.copyOfRange(path, 1, path.length);
			System.arraycopy(path, 1, subPath, 0, subPath.length);
			String nextKey = subPath[0];
			if (Maths.isInteger(nextKey)) {
				Arr newTarget = isDataArray(o) ? (Arr) o : target.putNewArray(k);
				writeScalarProperty(newTarget, subPath, val);
			} else {
				Obj newTarget = isDataObject(o) ? (Obj) o : target.putNewObject(k);
				writeScalarProperty(newTarget, subPath, val);
			}
		}
	}

	private void writeScalarProperty(Arr target, String[] path, Object val) {
		Integer k = Integer.parseInt(path[0]);
		if (path.length == 1) {
			target.put(k, val);
		} else {
			Object o = target.getLength() > k ? target.get(k) : null;
			// String[] subPath = Arrays.copyOfRange(path, 1, path.length);
			String[] subPath = new String[path.length - 1];// Arrays.copyOfRange(path, 1, path.length);
			System.arraycopy(path, 1, subPath, 0, subPath.length);
			String nextKey = subPath[0];
			if (Maths.isInteger(nextKey)) {
				Arr newTarget = isDataArray(o) ? (Arr) o : target.putNewArray(k);
				writeScalarProperty(newTarget, subPath, val);
			} else {
				Obj newTarget = isDataObject(o) ? (Obj) o : target.putNewObject(k);
				writeScalarProperty(newTarget, subPath, val);
			}
		}
	}

	public Map<String, ? super Object> toMapsAndLists(Obj obj) {
		Map<String,Object> res = new LinkedHashMap<>();
		if (obj != null) {
			for (String k : obj.getKeys()) {
				Object v = obj.get(k);
				Object vToPut = 
					v == null ? null
					: v instanceof Obj ? toMapsAndLists((Obj)v)
					: v instanceof Arr ? toMapsAndLists((Arr)v)
					: v;
				res.put(k, vToPut);
			}
		}
		return res;
	}
	public List<? super Object> toMapsAndLists(Arr arr) {
		List<? super Object> res = new LinkedList<>();
		if (arr != null) {
			for (Object v : arr) {
				Object vToPut = 
					v == null ? null
					: v instanceof Obj ? toMapsAndLists((Obj)v)
					: v instanceof Arr ? toMapsAndLists((Arr)v)
					: v;
				res.add(vToPut);
			}
		}
		return res;
	}
	
	public Map<String, ? super Object> toMapsAndSetLists(Obj obj) {
		Map<String,Object> res = new LinkedHashMap<>();
		if (obj != null) {
			for (String k : obj.getKeys()) {
				Object v = obj.get(k);
				Object vToPut = 
					v == null ? null
					: v instanceof Obj ? toMapsAndSetLists((Obj)v)
					: v instanceof Arr ? toMapsAndSetLists((Arr)v)
					: v;
				res.put(k, vToPut);
			}
		}
		return res;
	}
	public SetList<? super Object> toMapsAndSetLists(Arr arr) {
		SetList<? super Object> res = new SetList<>();
		if (arr != null) {
			for (Object v : arr) {
				Object vToPut = 
					v == null ? null
					: v instanceof Obj ? toMapsAndSetLists((Obj)v)
					: v instanceof Arr ? toMapsAndSetLists((Arr)v)
					: v;
				res.add(vToPut);
			}
		}
		return res;
	}
	
	public List<String> toStringsList(Arr arr, boolean allowNulls) {
		List<String> list = new ArrayList<>();
		if (arr != null) {
			for (Object obj : arr) {
				if (obj != null || allowNulls) {
					list.add(obj.toString());
				}
			}
		}
		return list;
	}
	public List<String> toStringsList(Arr arr) {
		return toStringsList(arr, true);
	}
	
	public Map<String, String> toStringsMap(Obj obj, boolean allowNulls) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (obj != null) {
			for (String key : obj.getKeys()) {
				Object v = obj.get(key);
				if (v != null) {
					map.put(key, v.toString());
				} else if (allowNulls) {
					map.put(key, null);
				}
			}
		}
		return map;
	}
	public Map<String, String> toStringsMap(Obj obj) {
		return toStringsMap(obj, true);
	}
	
	
	//if convertor#canConvert returns false value is not added to result
	@SuppressWarnings("unchecked")
	public <X extends DataStruct> X convertLeaves(X source, SafeConvertor<Object, Object> convertor) {
		return Check.notNull(source) instanceof Obj
			? (X)convertLeavesInternal((Obj)source, convertor)
			: (X)convertLeavesInternal((Arr)source, convertor);
	}

	private Obj convertLeavesInternal(Obj source, SafeConvertor<Object, Object> convertor) {
		Obj res = new DataObjectImpl();
		for (String k : source.getKeys()) {
			Object v = source.get(k);
			if(isPrimitiveOrNull(v) && convertor.canConvert(v)) {
				res.put(k, convertor.apply(v));
			} else if(v instanceof Obj) {
				res.put(k, convertLeavesInternal((Obj)v, convertor));
			} else {
				res.put(k, convertLeavesInternal((Arr)v, convertor));
			}
		}
		return res;
	}

	private Arr convertLeavesInternal(Arr source, SafeConvertor<Object, Object> convertor) {
		Arr res = new DataArrayImpl();
		int len = source.getLength();
		for (int i = 0; i < len; i++) {
			Object v = source.get(i);
			if(isPrimitiveOrNull(v) && convertor.canConvert(v)) {
				res.add(convertor.apply(v));
			} else if(v instanceof Obj) {
				res.add(convertLeavesInternal((Obj)v, convertor));
			} else {
				res.add(convertLeavesInternal((Arr)v, convertor));
			}
		}
		return res;
	}

	/**
	 * members in the form key, val, key, val etc.
	 */
	public Obj buildObject(Object... members) {
		Obj o = newObject();
		for (int i = 0; i < members.length; i+=2) {
			o.put((String)members[i], members[i+1]);
		}
		return o;
	}
	
	public Arr buildArray(Object... members) {
		Arr a = newArray();
		for (Object member : members) {
			a.add(member);
		}
		return a;
	}

	public Obj newObject() {
		return new DataObjectImpl();
	}
	public <T> Obj newObject(Iterable<T> iterable, Function<T, Tuple2<String, Object>> fnc) {
		final Obj obj = newObject();
		for (T item : iterable) {
			Tuple2<String, Object> v = Check.notNull(fnc.apply(item));
			obj.put(v.get0(), v.get1());
		}
		return obj;
	}
	public <T> Obj newObject(Iterable<T> iterable, BiFunction<T, Obj, Tuple2<String, Object>> fnc) {
		final Obj obj = newObject();
		for (T item : iterable) {
			Tuple2<String, Object> v = fnc.apply(item, obj);
			if (v != null) { // it's allowed to return null and directly manipulate obj from fnc
				obj.put(v.get0(), v.get1());
			}
		}
		return obj;
	}

	public Arr newArray() {
		return new DataArrayImpl();
	}
	public <T> Arr newArray(Iterable<T> iterable, Function<T, Object> fnc) {
		final Arr arr = newArray();
		for (T item : iterable) {
			arr.add(fnc.apply(item));
		}
		return arr;
	}

	public DataStruct parse(Reader json, boolean loose) {
		try {
			return loose ? new JsonLooseParser().parse(json) : new JSONParser().parse(json);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public DataStruct parseUtf8(InputStream json, boolean loose) {
		return parse(new InputStreamReader(json, Strings.UTF8), loose);
	}

	public DataStruct parse(String json, boolean loose) {
		return parse(new StringReader(json), loose);
	}

	public Obj parseObject(Reader json, boolean loose) {
		return (Obj)parse(json, loose);
	}

	public Obj parseObjectUtf8(InputStream json, boolean loose) {
		return (Obj)parseUtf8(json, loose);
	}

	public Obj parseObject(String json, boolean loose) {
		return (Obj)parse(new StringReader(json), loose);
	}

	public Arr parseArray(Reader json, boolean loose) {
		return (Arr)parse(json, loose);
	}

	public Arr parseArrayUtf8(InputStream json, boolean loose) {
		return (Arr)parseUtf8(json, loose);
	}

	public Arr parseArray(String json, boolean loose) {
		return (Arr)parse(new StringReader(json), loose);
	}

	public DataStruct parse(Reader json) {
		return parse(json, false);
	}

	public DataStruct parseUtf8(InputStream json) {
		return parseUtf8(json, false);
	}

	public DataStruct parse(String json) {
		return parse(json, false);
	}

	public Obj parseObject(Reader json) {
		return parseObject(json, false);
	}

	public Obj parseObjectUtf8(InputStream json) {
		return parseObjectUtf8(json, false);
	}

	public Obj parseObject(String json) {
		return (Obj)parse(new StringReader(json));
	}

	public Arr parseArray(Reader json) {
		return parseArray(json, false);
	}

	public Arr parseArrayUtf8(InputStream json) {
		return parseArrayUtf8(json, false);
	}

	public Arr parseArray(String json) {
		return parseArray(json, false);
	}

	// can lead to classcastexception in case it is not a dataobject array
	@SuppressWarnings("unchecked")
	public Iterable<Obj> asObjSeq(@Nullable Arr arr) {
		if (arr != null) {
			return (Iterable<Obj>)(Iterable<?>)arr;
		}
		return Collections.emptyIterable();
	}

	@SuppressWarnings("unchecked")
	public Iterable<Arr> asArrSeq(@Nullable Arr arr) {
		if (arr != null) {
			return (Iterable<Arr>)(Iterable<?>)arr;
		}
		return Collections.emptyIterable();
	}

	// can lead to classcastexception in case it is not a map of dataobjects
	public Iterable<Obj> asObjSeq(final @Nullable Obj obj) {
		return new Iterable<Obj>() {
			@Override public Iterator<Obj> iterator() {
				if (obj != null) {
					final Iterator<Entry<String, Object>> i = obj.iterator();
					return new Iterator<Obj>() {
						@Override public boolean hasNext() {
							return i.hasNext();
						}
						@Override public Obj next() {
							return (Obj) i.next().getValue();
						}
						@Override public void remove() {
							Check.unsupportedoperation.fail();
						}
					};
				}
				return java.util.Collections.emptyIterator();
			}
		};
	}
	
	// can lead to classcastexception in case it is not a map of dataobjects
	public Iterable<Tuple2<String, Obj>> asKeyObjSeq(final @Nullable Obj obj) {
		return new Iterable<Tuple2<String, Obj>>() {
			@Override public Iterator<Tuple2<String, Obj>> iterator() {
				if (obj != null) {
					final Iterator<Entry<String, Object>> i = obj.iterator();
					return new Iterator<Tuple2<String, Obj>>() {
						@Override public boolean hasNext() {
							return i.hasNext();
						}
						@Override public Tuple2<String, Obj> next() {
							Entry<String, Object> entry = i.next();
							return new Tuple2<String, Obj>(entry.getKey(), (Obj) entry.getValue());
						}
						@Override public void remove() {
							Check.unsupportedoperation.fail();
						}
					};
				}
				return java.util.Collections.emptyIterator();
			}
		};
	}
	
	// can lead to classcastexception if comparator is not of the right type
	@SuppressWarnings("unchecked")
	public <T> void sort(Arr arr, Comparator<T> c) {
		int l = arr.getLength();
		Object[] objs = new Object[l];
		for (int i=0; i<l; i++) {
			objs[i] = arr.get(i);
		}
		Arrays.sort((T[])objs, c);
		for (int i=0; i<l; i++) {
			arr.put(i, objs[i]);
		}
	}
	
	public Arr subArray(Arr arr, int from, int to) {
		Arr res = newArray();
		int limit = Math.min(arr.getLength(), to) - from;
		for (int i = 0; i < limit; i++) {
			res.put(i, arr.get(i + from));
		}
		return res;
	}

	public Arr arr(Object... attributes) {
		return STRUCT.buildArray(attributes);
	}

	public Obj obj(Object... attributes) {
		return STRUCT.buildObject(attributes);
	}

	public Obj obj(Map map) {
		return STRUCT.fromMapsAndCollections(map);
	}
}
