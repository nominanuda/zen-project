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

import static com.nominanuda.zen.obj.JsonSerializer.JSON_SERIALIZER;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.annotation.Nullable;

import com.nominanuda.zen.seq.ReadOnlyCollection;
import com.nominanuda.zen.stereotype.Value;

public class ObjImpl implements Obj {
	private final LinkedHashMap<Key,Any> members;

	ObjImpl(BinRange range, LinkedHashMap<Key, Any> m) {
		this(m);
	}
	ObjImpl() {
		this(new LinkedHashMap<>(16));
	}
	private ObjImpl(LinkedHashMap<Key, Any> m) {
		members = m;
	}

	@Override
	public int len() {
		return members.size();
	}

	//@Override
	public Iterator<Entry<String, Object>> iterator() {
		final Iterator<Entry<Key,Any>> i = members.entrySet().iterator();
		return new Iterator<Entry<String,Object>>() {
			public Entry<String, Object> next() {
				final Entry<Key,Any> e = i.next();
				return new Entry<String, Object>() {
					public Object setValue(Object value) {
						throw new UnsupportedOperationException();
					}
					public Object getValue() {
						return e.getValue().toJavaObjModel();
					}
					public String getKey() {
						return e.getKey().toString();
					}
				};
			}
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}
		};
	}

	private ReadOnlyCollection<? extends Entry<Key, ? extends Any>> members() {
		return ReadOnlyCollection.wrap(members.entrySet());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Any> T storeAny(Key k, Any val) {
		//Any copy = val.copy();
		members.put(k, val);
		return (T)val;
	}

	@Override
	public <T> T store(String k, @Nullable T v) {
		storeAny(Key.of(k), Any.toStruObjModel(v));
		return v;
	}

	@Override
	public Object fetch(String k) {
		Any a = members.get(Key.of(k));
		return a == null ? null : a.toJavaObjModel();
	}

	@Override
	public Any fetchAny(Key k) {
		Any a = members.get(k);
		return a == null ? Val.NULL : a;
	}

	@Override
	public Object del(String k) {
		Any removed = members.remove(Key.of(k));
		return removed == null ? null : removed.toJavaObjModel();
	}
	@Override
	public int push(String k, Object v) {
		return pushInternal(k, v, false);
	}

	private int pushInternal(String k, Object v, boolean recursive) {
		if(!recursive && v != null && v instanceof Arr) {
			Arr arr = (Arr)v;
			if(arr.len() == 0) {
				Key key = Key.of(k);
				Any a = members.get(key);
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
				res = pushInternal(k, x, true);
			}
			return res;
		} else {
			Key key = Key.of(k);
			Any newVal = Any.toStruObjModel(v);
			//illegalargument.assertTrue(newVal.isVal(), "only primitive types can be pushed");
			Any a = members.get(key);
			if(a == null) {
				members.put(key, newVal);
				return 1;
			} else if(a.isArr() && a.asArr().len() > 0) {
				Arr arr = a.asArr();
				arr.push(v);
				return arr.len();
			} else {
				Arr arr = newArr();
				arr.push(a);
				arr.push(v);
				members.put(key, arr);
				return 2;
			}
		}
	}
	/**
	 * 
	 * @param obj
	 * @return true if all members are {@link Value#nullSafeEquals(Object, Object)} ignoring order
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Obj)) {
			return false;
		}
		Obj other = (Obj)obj;
		if(other.len() != size()) {
			return false;
		}
		for(Entry<Key, ? extends Any> e : ((ObjImpl)other).members()) {
			if(! Value.nullSafeEquals(members.get(e.getKey()), e.getValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Obj copy() {
		LinkedHashMap<Key,Any> m = new LinkedHashMap<Key, Any>(size());
		for(Entry<Key, Any> entry : members.entrySet()) {
			m.put(entry.getKey().copy(), entry.getValue().copy());
		}
		return new ObjImpl(m);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result += prime * members.size();
		final Iterator<Entry<Key, Any>> i = members.entrySet().iterator();
		if(i.hasNext()) {
			final Entry<Key, Any> first = i.next();
			result += first.getKey().hashCode() + first.getValue().hashCode();
		}
		return result;
	}
	@Override
	public JsonType getType() {
		return JsonType.obj;
	}
	@Override
	public Obj reset() {
		members.clear();
		return this;
	}

//	default void stream(Arr array, JsonxContentHandler jch) throws RuntimeException {
//		jch.startArray();
//		int len = array.getLength();
//		for(int i = 0; i < len; i++) {
//			Object o = array.get(i);
//			streamItem(jch, o);
//		}
//		jch.endArray();
//	}
//
//	default void streamItem(JsonxContentHandler jch, Object o) throws RuntimeException {
//		switch (Stru.Stru.getDataType(o)) {
//		case array:
//			stream((Arr)o, jch);
//			break;
//		case object:
//			stream((Obj)o, jch);
//			break;
//		default:
//			jch.primitive(o);
//			break;
//		}
//	}
//	default void stream(Obj object, JsonxContentHandler jch) throws RuntimeException {
//		jch.startObject();
//		for(String k : object.getKeys()) {
//			Object o = object.get(k);
//			jch.startEntry(k);
//			streamItem(jch, o);
//			jch.endEntry();
//		}
//		jch.endObject();
//	}

//Map impl
	@Override
	public boolean containsKey(Object key) {
		return key instanceof String ? members.containsKey(Key.of((String)key)) : false;
	}
	@Override
	public boolean containsValue(Object value) {
		return members.containsValue(Any.toStruObjModel(value));
	}

	@Override
	public Set<String> keySet() {
		Iterator<Key> i = members.keySet().iterator();
		return new AbstractSet<String>() {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					public boolean hasNext() {
						return i.hasNext();
					}
					public String next() {
						return i.next().toString();
					}};
			}
			@Override
			public int size() {
				return len();
			}
		};
	}
	@Override
	public Collection<Object> values() {
		Iterator<Any> i = members.values().iterator();
		return ReadOnlyCollection.wrap(new Iterator<Object>() {
			public boolean hasNext() {
				return i.hasNext();
			}
			public Object next() {
				return i.next().toJavaObjModel();
			}
		});
	}
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return new AbstractSet<Entry<String,Object>>() {
			public Iterator<java.util.Map.Entry<String, Object>> iterator() {
				return ObjImpl.this.iterator();
			}
			public int size() {
				return len();
			}
		};
	}

	@Override
	public void sendTo(JixHandler sink) {
		sink.startObj();
		for(Entry<Key, Any> member : members.entrySet()) {
			member.getKey().sendTo(sink);
			member.getValue().sendTo(sink);
		}
		sink.endObj();
	}

	@Override
	public String toString() {
		return JSON_SERIALIZER.toString(this);
	}

	@Override
	public Obj newObj() {
		return new ObjImpl();
	}

	@Override
	public Arr newArr() {
		return new ArrImpl();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> TArr<T> newArr(Class<T> cl) {
		return (TArr<T>)new ArrImpl();
	}

}
