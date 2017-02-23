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

import static com.nominanuda.zen.seq.Seq.SEQ;

import java.util.Collection;
import java.util.Iterator;

import com.nominanuda.zen.stereotype.Value;

/**
 * an {@link Iterable} with {@link Collection#size()}
 * to create an instance just {@link ReadOnlyCollection#size()} and {@link ReadOnlyCollection#iterator()}
 * need to be implemented. That is, the rest of the collection interface is implemented by default methods.
 * Also static factory methods are provided for wrapping {@link Iterable}s and {@link Iterator}s that miss the size.
 * @param <E>
 */
public interface ReadOnlyCollection<E> extends Collection<E> {

	@Override
	int size();

	@Override
	Iterator<E> iterator();

	@Override
	default boolean isEmpty() {
		return ! iterator().hasNext();
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(contains(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default boolean contains(Object o) {
		Iterator<E> i = iterator();
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
	default boolean add(E e) {
		throw new UnsupportedOperationException("read-only collection");
	}


	@Override
	default boolean remove(Object o) {
		throw new UnsupportedOperationException("read-only collection");
	}

	@Override
	default boolean addAll(Collection<? extends E> c) {
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
	default void clear() {
		throw new UnsupportedOperationException("read-only collection");
	}


	public static <T> ReadOnlyCollection<T> wrap(int len, Iterable<T> iter) {
		return new BoundIterableImpl<T>(len, iter);
	}

	public static <T> ReadOnlyCollection<T> wrap(Collection<T> coll) {
		return new BoundIterableImpl<T>(coll.size(), coll);
	}

	public static <T> ReadOnlyCollection<T> wrap(Iterable<T> iter) {
		return wrap(iter.iterator());
	}

	public static <T> ReadOnlyCollection<T> wrap(Iterator<T> itr) {
		final LazyCopyIterator<T> i = new LazyCopyIterator<>(itr);
		boolean first = true;
		return new ReadOnlyCollection<T>() {
			private int len = -1;
			@Override
			public LazyCopyIterator<T> iterator() {
				return first ? i : i.copy();
			}
			@Override
			public int size() {
				if(len < 0) {
					len = i.toList().size();
				}
				return len;
			}
			@Override
			public boolean isEmpty() {
				return i.isEmpty();
			}
		};
	}

	public static class BoundIterableImpl<T> implements ReadOnlyCollection<T> {
		private final int len;
		private final Iterable<T> iter;

		public BoundIterableImpl(int len, Iterable<T> iter) {
			this.len = len;
			this.iter = iter;
		}

		@Override
		public Iterator<T> iterator() {
			return iter.iterator();
		}

		@Override
		public int size() {
			return len;
		}

		@Override
		public boolean isEmpty() {
			return len == 0;
		}
	}

}
