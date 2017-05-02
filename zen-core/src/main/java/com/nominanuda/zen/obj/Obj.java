/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.obj;

import static com.nominanuda.zen.common.Check.illegalargument;
import static com.nominanuda.zen.obj.JsonDeserializer.JSON_DESERIALIZER;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.stereotype.Value;

public interface Obj extends Stru, Iterable<Entry<String, Object>>, Map<String,Object>/*ReadOnlyCollection<Entry<String, Object>>,*/ {
	
	public static Obj make(Object...keysAndVals) {
		illegalargument.assertTrue(keysAndVals.length % 2 == 0, "odd number of arguments");
		ObjImpl o = new ObjImpl();
		if(keysAndVals != null) {
			int halflen = keysAndVals.length / 2;
			for(int i = 0; i < halflen; i++) {
				o.store((String) keysAndVals[i * 2], keysAndVals[i * 2 + 1]);
			}
		}
		return o;
	}

	<T> T store(String k, @Nullable T v);

	@Nullable
	Object fetch(String k);

	@Nullable
	Object del(String k);

	@Override
	default int len() {
		int i = 0;
		for(@SuppressWarnings("unused") Entry<String, Object> e : this) {
			i++;
		}
		return i;
	}

	@Override
	default Obj reset() {
		for(Entry<String, Object> e : this) {
			del(e.getKey());
		}
		return this;
	}

	@Override 
	default Obj copy() {
		return this;
	}

	@Override 
	default void sendTo(JixHandler sink) {
		sink.startObj();
		for(Entry<String, Object> e : this) {
			sink.key(Key.of(e.getKey()));
			Any.toStruObjModel(e.getValue()).sendTo(sink);
		}
		sink.endObj();
	}

	@SuppressWarnings("unchecked")
	default <T extends Any> T storeAny(Key k, Any val) {
		return (T)Any.toStruObjModel(store(k.get(), val.toJavaObjModel()));
	}

	default Any fetchAny(Key k) {
		return Any.toStruObjModel(fetch(k.get()));
	}
	
	default boolean exists(Object k) {
		return containsKey(k);
	}


	/**
	 * set the value if unset ({@link #indexOfKey(String)} == -1 ) or creates an {@link Arr}
	 * and pushes the value to it; if an {@link Arr} is already present simply
	 * pushes the new value to it; if the pushed value is an array itself every member is pushed
	 * <pre>
	 * {}.push(x, null)         -&gt; {x:null}
	 * {}.push(x, 1)            -&gt; {x:1}
	 * {}.push(x, {})           -&gt; {x:{}}
	 * {}.push(x, [])           -&gt; {}
	 * {}.push(x, [[]])         -&gt; {x:[]}
	 * {}.push(x, [[1]])        -&gt; {x:[1]}
	 * {}.push(x, [1])          -&gt; {x:1}
	 * {x:{}}.push(x, null)     -&gt; {x:[{},null]}
	 * {x:{y:1}}.push(x, 1)     -&gt; {x:[1,{y:1}]}
	 * {x:{y:1}}.push(x, {y:2}) -&gt; {x: [{y:2},{y:1}]}
	 * {x:{y:1}}.push(x, [])    -&gt; {x:{y:1}}
	 * {x:{y:1}}.push(x, [1,[]])-&gt; {x: [{y:1},1,[]]}
	 * </pre>
	 * @param k
	 * @param v
	 * @return the cardinality 
	 * @throws IllegalArgumentException
	 */
//	int push(String k, @Nullable Object v) throws IllegalArgumentException;

	default int push(String k, Object v) {
		return pushImpl(this, k, v, false);
	}

	//not to be called from ouside
	default int pushImpl(Obj o, String k, Object v, boolean recursive) {
		if(!recursive && v != null && v instanceof Arr) {
			Arr arr = (Arr)v;
			if(arr.len() == 0) {
				Key key = Key.of(k);
				Any a = o.fetchAny(key);
				if(a == null) {
					return 0;
				} else if(a.isArr()) {
					return a.asArr().len();
				} else {
					return 1;
				}
			}
			int res = 0;
			for(Object x : (Arr)v) {
				res = pushImpl(o, k, x, true);
			}
			return res;
		} else {
			Key key = Key.of(k);
			Any newVal = Any.toStruObjModel(v);
			//illegalargument.assertTrue(newVal.isVal(), "only primitive types can be pushed");
			Any a = o.fetchAny(key);
			if(a == null) {
				o.storeAny(key, newVal);
				return 1;
			} else if(a.isArr() && a.asArr().len() > 0) {
				Arr arr = a.asArr();
				arr.push(v);
				return arr.len();
			} else {
				Arr arr = newArr();
				arr.push(a);
				arr.push(v);
				o.storeAny(key, arr);
				return 2;
			}
		}
	}

	@Override
	default JsonType getType() {
		return JsonType.obj;
	}

	///////////Collection
	/// other accessors
	default Obj obj(String k) throws ClassCastException {
		return (Obj)fetch(k);
	}

	default Arr arr(String k) throws ClassCastException {
		return (Arr)fetch(k);
	}

	default String str(String k) throws ClassCastException {
		return (String)fetch(k);
	}

	default Obj with(String k, Object v) {
		store(k, v);
		return this;
	}

	default Arr newArr(String key) {
		Arr a = newArr();
		store(key, a);
		return a;
	}
	default Obj newObj(String key) {
		Obj o = newObj();
		store(key, o);
		return o;
	}

//TODO	default Integer getInt(String k) throws ClassCastException {
//		return ((Long)fetch(k)).;
//	}

	////Map
	@Override
	default boolean containsKey(Object key) {
		if (key instanceof String) {
			for (Entry<String, Object> x : this) {
				if (key.equals(x.getKey())) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	default boolean containsValue(Object value) {
		for (Entry<String, Object> x : this) {
			if (Value.nullSafeEquals(value, x.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	default int size() {
		return len();
	}
	@Override
	default boolean isEmpty() {
		return len() == 0;
	}
	@Override
	default Object get(Object key) {
		return key instanceof String ? fetch((String)key) : null;
	}
	@Override
	default Object put(String key, Object value) {
		Object ret = fetch(key);
		store(key, value);
		return ret;
	}
	@Override
	default Object remove(Object key) {
		return key instanceof String ? del((String)key) : null;
	}
	@Override
	default void clear() {
		reset();
	}

	@Override
	default void putAll(Map<? extends String, ? extends Object> m) {
		for(Entry<? extends String, ? extends Object> e : m.entrySet()) {
			store(e.getKey(), e.getValue());
		}
	}
	@Override
	default Set<String> keySet() {
		LinkedHashSet<String> s = new LinkedHashSet<>();
		this.forEach((e) -> s.add(e.getKey()));
		return s;
	}

	@Override
	default Collection<Object> values() {
		LinkedList<Object> l = new LinkedList<>();
		this.forEach((e) -> l.add(e.getValue()));
		return l;
	}

	@Override
	default Set<Entry<String, Object>> entrySet() {
		LinkedHashSet<Entry<String, Object>> s = new LinkedHashSet<>();
		this.forEach((e) -> s.add(e));
		return s;
	}

	//Convenience Methods
	default String getStr(String k) {
		return (String)fetch(k);
	}
	default String getStrictStr(String key) {
		return (String)getStrict(key);
	}

	default Obj getObj(String k) {
		return (Obj)fetch(k);
	}
	default Obj getStrictObj(String k) {
		return (Obj)getStrict(k);
	}

	default Arr getArr(String k) {
		return (Arr)fetch(k);
	}
	default Arr getStrictArr(String k) {
		return (Arr)getStrict(k);
	}
	
	default Number getNum(String k) {
		return (Number)fetch(k);
	}
	default Number getStrictNum(String k) {
		return (Number)getStrict(k);
	}
	default Double getDouble(String k) {
		Number n = getNum(k);
		return n != null ? n.doubleValue() : null;
	}
	default Double getStrictDouble(String k) {
		Number n = getStrictNum(k);
		return n != null ? n.doubleValue() : null;
	}
	default Integer getInt(String k) {
		Number n = getNum(k);
		return n != null ? n.intValue() : null;
	}
	default Integer getStrictInt(String k) {
		Number n = getStrictNum(k);
		return n != null ? n.intValue() : null;
	}
	default Long getLong(String k) {
		Number n = getNum(k);
		return n != null ? n.longValue() : null;
	}
	default Long getStrictLong(String k) {
		Number n = getStrictNum(k);
		return n != null ? n.longValue() : null;
	}

	default Boolean getBoolean(String k) {
		return (Boolean)fetch(k);
	}
	default boolean getStrictBoolean(String k) {
		return Boolean.valueOf(getBoolean(k));
	}

	public static Obj parse(InputStream is) {
		return (Obj)JSON_DESERIALIZER.deserialize(is);
	}

	public static Obj parse(Reader r) {
		return (Obj)JSON_DESERIALIZER.deserialize(r);
	}

	public static Obj parse(String r) {
		return (Obj)JSON_DESERIALIZER.deserialize(r);
	}

	default Object getStrict(String k) {
		return Check.notNull(fetch(k));
	}

	default Obj putObj(String key) {
		Obj o = newObj();
		store(key, o);
		return o;
	}

	default Arr putArr(String key) {
		Arr arr = newArr();
		store(key, arr);
		return arr;
	}

	static Obj fromMap(Map<?, ?> m) {
		Obj o = make();
		StruUtils.deepCopy(m, o);
		return o;
	}

	@SuppressWarnings("unchecked")
	default <K> Map<String,K> of(Class<K> klass) {
		return (Map<String,K>)this;
	}
	
	default Map<String,Obj> ofObj() {
		return of(Obj.class);
	}
	public static Map<String,Obj> ofObj(@Nullable Obj o) {
		return (o != null ? o : make()).ofObj();
	}
	
	default Map<String,Arr> ofArr() {
		return of(Arr.class);
	}
	public static Map<String,Arr> ofArr(@Nullable Obj o) {
		return (o != null ? o : make()).ofArr();
	}
	
	default Map<String,String> ofStr() {
		return of(String.class);
	}
	public static Map<String,String> ofStr(@Nullable Obj o) {
		return (o != null ? o : make()).ofStr();
	}
}
