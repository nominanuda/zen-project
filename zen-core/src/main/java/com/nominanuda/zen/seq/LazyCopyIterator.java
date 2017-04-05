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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class LazyCopyIterator<E> implements ListIterator<E> {
	private final Iterator<E> i;
	private final LinkedList<E> l;
	private ListIterator<E> litr;

	public LazyCopyIterator(Iterator<E> itr) {
		i = itr;
		l = new LinkedList<E>();
	}
	private LazyCopyIterator(LinkedList<E> ll) {
		i = null;
		l = ll;
		litr = l.listIterator();
	}
	public boolean isEmpty() {
		if(l.size() > 0) {
			return false;
		} else if(i == null) {
			return true;
		} else {
			return i.hasNext();
		}
	}

	public LazyCopyIterator<E> copy() {
		getOrExplodeListItr();
		return new LazyCopyIterator<E>(l);
	}
	public LinkedList<E> toList() {
		getOrExplodeListItr();
		return SEQ.copy(l);
	}

	public boolean isExploded() {
		return litr != null;
	}
	private ListIterator<E> getOrExplodeListItr() {
		if(litr == null) {
			int soFar = l.size();
			while(i.hasNext()) {
				l.add(i.next());
			}
			litr = l.listIterator(soFar);
		}
		return litr;
	}

	@Override
	public boolean hasNext() {
		if(litr != null) {
			return litr.hasNext();
		} else {
			return i.hasNext();
		}
	}

	@Override
	public E next() {
		if(litr != null) {
			return litr.next();
		} else {
			E e = i.next();
			l.add(e);
			return e;
		}
	}

	@Override
	public boolean hasPrevious() {
		return getOrExplodeListItr().hasPrevious();
	}

	@Override
	public E previous() {
		return getOrExplodeListItr().previous();
	}
	@Override
	public int nextIndex() {
		return getOrExplodeListItr().nextIndex();
	}
	@Override
	public int previousIndex() {
		return getOrExplodeListItr().previousIndex();
	}
	@Override
	public void remove() {
//		getOrExplodeListItr().remove();
		throw new UnsupportedOperationException("read-only collection");
	}
	@Override
	public void set(E e) {
//		getOrExplodeListItr().set(e);
		throw new UnsupportedOperationException("read-only collection");
	}
	@Override
	public void add(E e) {
//		getOrExplodeListItr().add(e);
		throw new UnsupportedOperationException("read-only collection");
	}
}