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
package com.nominanuda.zen.seq;

import static com.nominanuda.zen.common.Check.unsupportedoperation;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

@ThreadSafe
public class Seq {
	public static final Seq SEQ = ScopedSingletonFactory.getInstance().buildJvmSingleton(Seq.class);

	public boolean nullOrEmpty(@Nullable Iterable<?> coll) {
		if(coll == null) {
			return true;
		} else if(coll instanceof Collection) {
			return ((Collection<?>)coll).size() == 0;
		} else {
			return ! coll.iterator().hasNext();
		}
	}

	@SuppressWarnings("unchecked")
	public <K,V> Map<K,V> buildMap(@SuppressWarnings("rawtypes") Class<? extends Map> mclass, Object... members) throws IllegalArgumentException {
		int len = members.length;
		if(len % 2 != 0) {
			throw new IllegalArgumentException("odd number of arguments");
		}
		try {
			Map<K,V> m = mclass.newInstance();
			for(int i = 0; i < len; i += 2) {
				m.put((K)members[i], (V)members[i+1]);
			}
			return m;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public <T> HashSet<T> hashSet(@SuppressWarnings("unchecked") T... ts) {
		return (HashSet<T>)buildSet(HashSet.class, ts);
	}
	public <T> LinkedHashSet<T> linkedHashSet(@SuppressWarnings("unchecked") T... ts) {
		return (LinkedHashSet<T>)buildSet(LinkedHashSet.class, ts);
	}

	@SuppressWarnings("unchecked")
	public <K,V> LinkedHashMap<K,V> linkedHashMap(Object... ts) {
		return (LinkedHashMap<K,V>)buildMap(LinkedHashMap.class, ts);
	}

	public <T> LinkedList<T> linkedList(Iterable<T> c) {
		LinkedList<T> l = new LinkedList<>();
		for(T t : c) {
			l.add(t);
		}
		return l;
	}

	public <T> LinkedList<T> linkedList(@SuppressWarnings("unchecked") T... ts) {
		return (LinkedList<T>)buildList(LinkedList.class, ts);
	}

	@SuppressWarnings("unchecked")
	public <T> Set<T> buildSet(@SuppressWarnings("rawtypes") Class<? extends Set> sclass, T...ts) {
		try {
			Set<T> s = sclass.newInstance();
			for(T t : ts) {
				s.add(t);
			}
			return (Set<T>)s;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> buildList(@SuppressWarnings("rawtypes") Class<? extends List> sclass, T...ts) {
		try {
			List<T> s = sclass.newInstance();
			for(T t : ts) {
				s.add(t);
			}
			return s;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> boolean find(T needle, Iterable<? extends T> haystack) {
		for(T x : haystack) {
			if(needle.equals(x)) {
				return true;
			}
		}
		return false;
	}

	public <T> boolean find(T needle, T[] haystack) {
		for(T x : haystack) {
			if(needle.equals(x)) {
				return true;
			}
		}
		return false;
	}
	public <K,V> MapEntry<K, V> mapEntry(K k, V v) {
		return new MapEntry<K, V>(k, v);
	}

	public class MapEntry<K,V> implements Entry<K, V> {
		private final K k;
		private final V v;

		public MapEntry<K, V> of(K k, V v) {
			return new MapEntry<K, V>(k, v);
		}

		public MapEntry(K k, V v) {
			this.k = k;
			this.v = v;
		}
		public V setValue(V arg0) {
			unsupportedoperation.fail();
			return null;
		}
		public V getValue() {
			return v;
		}
		public K getKey() {
			return k;
		}
	}

	public <T> ReadOnlyCollection<? extends T> toSizedIterable(final Iterable<T> iter) {
		return ReadOnlyCollection.wrap(iter);
	}

	@SuppressWarnings("unchecked")
	public <T,K extends Collection<? extends T>> K copyAndReverse(K coll) throws RuntimeException /* wrapping InstantiationException, IllegalAccessException*/ {
		try {
			K res = (K) coll.getClass().newInstance();
			if(res instanceof List) {
				((Collection<? super T>)res).addAll(coll);
				Collections.reverse((List<?>)res);
			} else {
				List<T> l = copyToList(((Collection<T>)coll));
				Collections.reverse((List<?>)l);
				((Collection<? super T>)res).addAll(l);
			}
			return res;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T,K extends Collection<? extends T>> K copy(K coll) throws RuntimeException /* wrapping InstantiationException, IllegalAccessException*/ {
		try {
			K res = (K) coll.getClass().newInstance();
			((Collection<? super T>)res).addAll(coll);
			return res;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> LinkedList<T> copyToList(Iterable<T> iter) {
		LinkedList<T> l = new LinkedList<>();
		for(T t : iter) {
			l.add(t);
		}
		return l;
	}

	public <T> Iterable<T> asIterable(final Iterator<T> iterator) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return iterator;
			}
		};
	}

	public <T> ReadOnlyCollection<T> asReadOnlyCollection(final Iterable<T> iterator) {
		return ReadOnlyCollection.wrap(iterator);
	}

	public int writeToArray(Iterable<? extends Object> iter, Object[] arr) {
		int idx = 0;
		int max = arr.length;
		for(Object o : iter) {
			arr[idx++] = o;
			if(max == idx) {
				break;
			}
		}
		return idx;
	}

	public <X> X[] toTypedArray(Collection<? extends Object> coll, Class<X> elementType) {
		@SuppressWarnings("unchecked")
		X[] arr = (X[]) Array.newInstance(elementType, coll.size());
		writeToArray(coll, arr);
		return arr;
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> emptyImmutbleSet() {
		return (Collection<T>)Collections.EMPTY_SET;
	}

	public <T> List<T> emptyList() {
		return Collections.emptyList();
	}

	public Map<String, Object> emptyMap() {
		return Collections.emptyMap();
	}

	public <T> List<T> fixedList(T... ts) {
		return Arrays.asList(ts);
	}

	public <X,Y> List<Y> mapToList(Collection<X> l, Function<X, Y> mapper) {
		return l.stream().map(mapper).collect(Collectors.toList());
	}
	
	public <T> List<T> reversedClone(List<T> list) {
		try {
			@SuppressWarnings("unchecked")
			List<T> l2 = list.getClass().newInstance();
			l2.addAll(list);
			java.util.Collections.reverse(l2);
			return l2;
		} catch (Exception e) {
			return null;
		}
	}

}
