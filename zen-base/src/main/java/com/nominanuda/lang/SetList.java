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
package com.nominanuda.lang;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

public class SetList<E> extends AbstractList<E> implements Set<E> {
	private LinkedList<E> l = new LinkedList<E>();

	@Override
	public E get(int index) {
		return l.get(index);
	}

	@Override
	public int size() {
		return l.size();
	}
	
	//TODO
	@Override
	public E set(int index, E element) {
		int i = indexOf(element);
		if(-1 == i) {
			return l.set(index, element);
		} else {
			Check.unsupportedoperation.fail();
			return null;
		}
	}
	@Override
	public void add(int index, E element) {
		if(-1 == indexOf(element)) {
			l.add(index, element);
		}
	}
	@Override
	public Spliterator<E> spliterator() {
		 return Spliterators.spliterator(this, Spliterator.DISTINCT);
	}
}
