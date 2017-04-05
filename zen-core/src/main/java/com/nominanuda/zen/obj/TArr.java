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

import static com.nominanuda.zen.seq.Seq.SEQ;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nullable;

import com.nominanuda.zen.seq.LazyCopyIterator;
import com.nominanuda.zen.stereotype.Value;

public interface TArr<T> extends Stru, List<T> {

	public static <T> TArr<T> make(Class<T> cl, Object...vals) {
		return Arr.makeTyped(cl);
	}

	/**
	 * @see {@link List#set(int, Object)} */
//	@Override
	@Nullable <X> X store(int idx, @Nullable X v);

	@Nullable
	T fetch(int idx);

	default Any fetchAny(int idx) {
		return Any.toStruObjModel(fetch(idx));
	}

	@SuppressWarnings("unchecked")
	default <X extends Any> X pushAny(X any) {
		push((T)any.toJavaObjModel());
		return any;
	}

	T del(int idx);

	@Override
	int len();

	@Override
	default TArr<T> copy() {
		return this;
	}

	@Override
	default int indexOf(Object v, int start) {
		int i = 0;
		for(Object o : this) {
			if(Value.nullSafeEquals(o, v)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	@Override
	default void sendTo(JixHandler sink) {
		sink.startArr();
		for(Object o : this) {
			Any.toStruObjModel(o).sendTo(sink);
		}
		sink.endArr();
	}

	/**
	 * 
	 * @param v
	 * @return the supplied Object
	 */
	<X extends T> X push(X v);

	default boolean isValArr() {
		for(Object x : this) {
			if(! JsonType.isNullablePrimitive(x)) {
				return false;
			}
		}
		return true;
	}

	@Override
	Arr reset();


	@Override
	default JsonType getType() {
		return JsonType.arr;
	}

	default TArr<T> with(T val) {
		push(val);
		return this;
	}

	//	default List<? extends T> asList() {
//		return this;
//	}

	///////////////////List
	@Override
	default int size() {
		return len();
	}
	@Override
	default T set(int index, Object element) {
		T ret = fetch(index);
		store(index, element);
		return ret;
	}

	@Override
	default void clear() {
		reset();
	}



	@Override
	default T get(int index) {
		return fetch(index);
	}

	@Override
	default void add(int index, Object element) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	default T remove(int index) {
		return del(index);
	}

	@Override
	default int indexOf(Object o) {
		return indexOf(o, 0);
	}

	@Override
	default boolean add(T e) {
		push((T)e);
		return true;
	}


	@Override
	default boolean remove(Object o) {
		int i = indexOf(o);
		if(i >= 0) {
			remove(i);
			return true;
		} else {
			return false;
		}
	}

	@Override
	default boolean isEmpty() {
		return size() == 0;
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(! contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	default boolean contains(Object o) {
		Iterator<T> i = iterator();
		while(i.hasNext()) {
			if(Value.nullSafeEquals(o, i.next())) {
				return true;
			}
		}
		return false;
	}

	@Override
	default Object[] toArray() {
		Object[] result = new Object[size()];
		SEQ.writeToArray(this, result);
		return result;
	}

	default <X> X[] toTypedArray(Class<X> elementType) {
		return SEQ.toTypedArray(this, elementType);
	}


	@Override
	@SuppressWarnings("unchecked")
	default <X> X[] toArray(X[] a) {
		int len = size();
		if(a.length < len) {
			return (X[])toTypedArray(a.getClass().getComponentType());
		} else {
			SEQ.writeToArray(this, a);
			return a;
		}
	}


	@Override
	default boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("read-only collection");
	}


	@Override
	default boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("read-only collection");
	}


	@Override
	default boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("read-only collection");
	}



	@Override
	default int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("read-only collection");
	}

	@Override
	ListIterator<T> listIterator();


	@Override
	default ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("read-only collection");
	}

	@Override
	default List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("read-only collection");
	}
	@Override
	default boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("read-only collection");
	}

	@SuppressWarnings("unchecked")
	default <X> List<X> asListOf(Class<X> class1) {
		return (List<X>) this;
	}

}
