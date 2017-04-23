package com.nominanuda.zen.obj;

import static com.nominanuda.zen.common.Maths.MATHS;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Ex.NoException;

public class JsonPath {
//	private enum MergeFeature {
//		arrayAppend,arrayMerge,arraySet,nullLeafAsNoValue,leafPush,leafSet, emptyScalarAsNull
//	}
	public static final JsonPath JPATH = new JsonPath();
	public static final int MERGE_POLICY_OVERRIDE = 0;
	public static final int MERGE_POLICY_PUSH = 1;
	private static final String MULTIVALUE_SUFFIX = "[]";
	private static final int MULTIVALUE_SUFFIX_LEN = MULTIVALUE_SUFFIX.length();

	public void copy(Obj src, Obj dst, int policy)
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

	public void copyOverwite(Obj source, Obj target) {
		Iterator<Entry<String, Object>> itr = source.iterator();
		while (itr.hasNext()) {
			Entry<String, Object> etr = itr.next();
			String key = etr.getKey();
			Object o = etr.getValue();
			if (JsonType.isNullablePrimitive(o)) {
				target.store(key, o);
			} else if (JsonType.isArr(o)) {
				copyOverwite((Arr) o, target.newArr(key));
			} else if (JsonType.isObj(o)) {
				copyOverwite((Obj) o, target.newObj(key));
			} else {
				throw new IllegalStateException(
						o.getClass().getName()
								+ " is neither a Stru nor a primitive type or null");
			}
		}
	}

	public void copyOverwite(Arr source, Arr target) {
		clear(target);
		for(Object o : source) {
			if (JsonType.isNullablePrimitive(o)) {
				target.push(o);
			} else if (JsonType.isArr(o)) {
				copyOverwite((Arr) o, target.pushArr());
			} else if (JsonType.isObj(o)) {
				copyOverwite((Obj) o, target.pushObj());
			} else {
				throw new IllegalStateException(
						o.getClass().getName()
								+ " is neither a Stru nor a primitive type or null");
			}
		}
	}
	private void clear(Arr target) {
		int len = target.len();
		for(int i = len - 1; i >= 0; i--) {
			target.del(i);
		}
		
	}

//	private void clear(Obj target) {
//		target.reset();
//	}

	/**
	 * {a:1} {b:1} =&gt; {a:1, b:1} {a:1} {a:1} =&gt; {a:[1, 1]} {a:1} {a:null} =&gt;
	 * {a:[1, null]} {a:{b:1}} {a:null} =&gt; {a:[{b:1}, null]} {a:{b:1}}
	 * {a:{b:{c:null}}} =&gt; {a:{b:[1,{c:null}]}} {a:1} {a:[2]} =&gt; {a:[1,2]}
	 * {a:[1]} {a:[2]} =&gt; {a:[1,2]} {a:{b:1}} {a:[2]} =&gt; {a:[{b:1},2]} {a:{b:1}}
	 * {a:{b:[2]}} =&gt; {a:{b:[1,2]}} {a:1} {} =&gt; {a:1}}
	 */
	public void copyPush(Obj source, Obj target) {
		Iterator<Entry<String,Object>> itr = source.iterator();
		while (itr.hasNext()) {
			Entry<String,Object> e = itr.next();
			putPush(target, e.getKey(), e.getValue());
		}
	}

	private void putPush(Obj target, String key, Object val) {
		if (target.exists(key)) {
			Object tval = target.fetch(key);
			if (JsonType.isObj(val) && JsonType.isObj(tval)) {
				copyPush((Obj) val, (Obj) tval);
			} else if (JsonType.isArr(tval)) {
				((Arr) tval).push(val);
			} else {
				Arr a = target.newArr(key);
				a.push(tval);
				a.push(val);
			}
		} else {
			target.store(key, val);
		}
	}

	public Object getPathSafe(Stru ds, String... pathBits) {
		Stru _target = ds;
		int len = pathBits.length;
		for(int i = 0; i < len; i++) {
			String pathBit = pathBits[i];
			Object k = toStringOrIntKey(pathBit);
			Object newTarget = _get(_target, k);
			if(JsonType.isNullablePrimitive(newTarget)) {
				return i == len - 1 ? newTarget : null;
			} else {
				_target = (Stru)newTarget;
			}
		}
		return _target;
	}
	public Object getPathSafe(Stru ds, String path) {
		return getPathSafe(ds, path.split("\\."));
	}
	public String getPathSafeStr(Stru ds, String... pathBits) {
		return (String)getPathSafe(ds, pathBits);
	}
	public String getPathSafeStr(Stru ds, String path) {
		return (String)getPathSafe(ds, path);
	}
	public Number getPathSafeNum(Stru ds, String... pathBits) {
		return (Number)getPathSafe(ds, pathBits);
	}
	public Number getPathSafeNum(Stru ds, String path) {
		return (Number)getPathSafe(ds, path);
	}
	public Obj getPathSafeObj(Stru ds, String... pathBits) {
		return (Obj)getPathSafe(ds, pathBits);
	}
	public Obj getPathSafeObj(Stru ds, String path) {
		return (Obj)getPathSafe(ds, path);
	}
	public Arr getPathSafeArr(Stru ds, String... pathBits) {
		return (Arr)getPathSafe(ds, pathBits);
	}
	public Arr getPathSafeArr(Stru ds, String path) {
		return (Arr)getPathSafe(ds, path);
	}
	
	private Object _get(Stru s, Object intOrStringKey) {
		if (s.isArr()) {
			Arr a = s.asArr();
			int k = (int) intOrStringKey;
			return k < a.len() ? a.fetch(k) : null;
		} else {
			return s.asObj().fetch((String)intOrStringKey);
		}
	}
	private boolean _exists(Stru s, Object intOrStringKey) {
		if (s.isArr()) {
			return s.asArr().exists((int)intOrStringKey);
		} else {
			return s.asObj().exists((String)intOrStringKey);
		}
	}
	private void _put(Stru s, Object intOrStringKey, Object val) {
		if (s.isArr()) {
			Arr a = s.asArr();
			int k = (int) intOrStringKey;
			int d = k - a.len();
			if (d < 0) {
				a.store(k, val);
			} else {
				for (int i = 0; i < d; i++) {
					a.add(null);
				}
				a.add(val);
			}
		} else {
			s.asObj().store((String)intOrStringKey, val);
		}
	}
	private Object toStringOrIntKey(String s) {
		Check.notNull(s);
		return MATHS.isInteger(s) ? Integer.valueOf(s) : s;
	}

	public void setOrPushProperty(Stru ds, Object key, @Nullable Object value) {
		if (_exists(ds, key)) {
			Object cur = _get(ds, key);
			if (JsonType.isArr(cur)) {
				((Arr)cur).push(value);
			} else if (ds.isArr() && cur == null) {
				_put(ds, key, value);
			} else {
				Arr darr = Arr.make();
				darr.push(cur);
				darr.push(value);
				_put(ds, key, darr);
			}
		} else {
			_put(ds, key, value);
		}
	}
	public void setPathProperty(Stru ds, String path, @Nullable Object value) {
		String[] pathBits = path.split("\\.");
		_put(explodePath(ds, path), toStringOrIntKey(pathBits[pathBits.length - 1]), value);
	}
	public void setOrPushPathProperty(Stru ds, String path, @Nullable  Object value) {
		String[] pathBits = path.split("\\.");
		setOrPushProperty(explodePath(ds, path), toStringOrIntKey(pathBits[pathBits.length - 1]), value);
	}

	private Stru explodePath(Stru ds, String path) {
		Stru _target = ds;
		String[] pathBits = path.split("\\.");
		int len = pathBits.length;
		for (int i = 0; i < len - 1; i++) {
			String pathBit = pathBits[i];
			Object k = toStringOrIntKey(pathBit);
			Object newTarget = _get(_target, k);
			if (JsonType.isNullablePrimitive(newTarget)) {
				if (MATHS.isInteger(pathBits[i + 1])) {
					newTarget = Arr.make();
				} else {
					newTarget = Obj.make();
				}
				_put(_target, k, newTarget);
			}
			_target = (Stru)newTarget;
		}
		return _target;
	}

	//if convertor#canConvert returns false value is not added to result
	@SuppressWarnings("unchecked")
	public <X extends Stru> X convertLeaves(X source, SafeConvertor<Object, Object> convertor) {
		return Check.notNull(source) instanceof Obj
			? (X)convertLeavesInternal((Obj)source, convertor)
			: (X)convertLeavesInternal((Arr)source, convertor);
	}

	private Obj convertLeavesInternal(Obj source, SafeConvertor<Object, Object> convertor) {
		Obj res = Obj.make();
		for(Entry<String, Object> e : source) {
			String k = e.getKey();
			Object v = e.getValue();
			if(JsonType.isNullablePrimitive(v) && convertor.canConvert(v)) {
				res.store(k, convertor.apply(v));
			} else if(v instanceof Obj) {
				res.store(k, convertLeavesInternal((Obj)v, convertor));
			} else {
				res.store(k, convertLeavesInternal((Arr)v, convertor));
			}
		}
		return res;
	}

	private Arr convertLeavesInternal(Arr source,
			SafeConvertor<Object, Object> convertor) {
		Arr res = Arr.make();
		int len = source.len();
		for(int i = 0; i < len; i++) {
			Object v = source.fetch(i);
			if(JsonType.isNullablePrimitive(v) && convertor.canConvert(v)) {
				res.push(convertor.apply(v));
			} else if(v instanceof Obj) {
				res.push(convertLeavesInternal((Obj)v, convertor));
			} else {
				res.push(convertLeavesInternal((Arr)v, convertor));
			}
		}
		return res;
	}

	public interface ObjectConvertor<X, Y, E extends Exception> {
		Y apply(X x) throws E;

		boolean canConvert(Object o);
	}

	public interface SafeConvertor<X, Y> extends ObjectConvertor<X, Y, NoException>, Function<X, Y> {
	}

	public void toFlatMap(Stru from, Map<String, Object> map) {
		toFlatMap(map, "", from);
	}

	private void toFlatMap(Map<String, Object> map, String key, @Nullable Object value) {
		switch (JsonType.of(value)) {
		case obj:
			Obj obj = (Obj) value;
			for (String k : obj.keySet()) {
				Object val = obj.getStrict(k);
				toFlatMap(map, keyJoin(key, k), val);
			}
			break;
		case arr:
			Arr arr = ((Arr) value);
			int len = arr.len();
			for (int i = 0; i < len; i++) {
				toFlatMap(map, keyJoin(key, i), arr.get(i));
			}
			break;
		case nil:
		case num:
		case bool:
		case str:
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
	

	public Stru fromFlatMap(Map<String, Object> map) {
		Obj dest = Obj.make();
		for (Entry<String, ?> e : map.entrySet()) {
			writeScalarOrMultivaluedProperty(dest, e.getKey(), e.getValue());
		}
		return dest;
	}
	
	private void writeScalarOrMultivaluedProperty(Obj target, String path, Object val) {
		if (val != null && val.getClass().isArray()) {
			val = Arrays.asList((Object[]) val);
		}
		if (!(val == null || val instanceof Collection<?>)) { // scalar
			if (path.endsWith(MULTIVALUE_SUFFIX)) {
				path = path.substring(0, path.length() - MULTIVALUE_SUFFIX_LEN) + ".0";
			}
			writeScalarProperty(target, path, val);
		} else { // Collection
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
			if (MATHS.isInteger(nextKey)) {
				Arr newTarget = JsonType.isArr(o) ? (Arr) o : target.arr(k);
				writeScalarProperty(newTarget, subPath, val);
			} else {
				Obj newTarget = JsonType.isObj(o) ? (Obj) o : target.obj(k);
				writeScalarProperty(newTarget, subPath, val);
			}
		}
	}

	private void writeScalarProperty(Arr target, String[] path, Object val) {
		Integer k = Integer.parseInt(path[0]);
		if (path.length == 1) {
			target.store(k, val);
		} else {
			Object o = target.len() > k ? target.get(k) : null;
			// String[] subPath = Arrays.copyOfRange(path, 1, path.length);
			String[] subPath = new String[path.length - 1];// Arrays.copyOfRange(path, 1, path.length);
			System.arraycopy(path, 1, subPath, 0, subPath.length);
			String nextKey = subPath[0];
			if (MATHS.isInteger(nextKey)) {
				Arr newTarget = JsonType.isArr(o) ? (Arr) o : target.store(k, Arr.make());
				writeScalarProperty(newTarget, subPath, val);
			} else {
				Obj newTarget = JsonType.isObj(o) ? (Obj) o : target.store(k, Obj.make());
				writeScalarProperty(newTarget, subPath, val);
			}
		}
	}

}