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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.nominanuda.zen.seq.ReadOnlyCollection;
import com.nominanuda.zen.stereotype.Value;

public class ArrImpl implements Arr {
	private final List<Any> members;

	public ArrImpl(BinRange range, List<Any> members) {
		this(members);
	}

	public ArrImpl() {
		this(new LinkedList<>());
	}

	private ArrImpl(List<Any> members) {
		this.members = members;
	}

	@Override
	public int indexOf(Object v, int start) {
		int len = size();
		for(int i = start; i < len; i++) {
			if(Value.nullSafeEquals(v, members.get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Iterator<Object> iterator() {
		final Iterator<Any> i = members.iterator();
		return new Iterator<Object>() {
			public Object next() {
				final Any e = i.next();
				return e.toJavaObjModel();
			}
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}
		};
	}

	ReadOnlyCollection<? extends Any> members() {
		return ReadOnlyCollection.wrap(members);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object store(int idx, Object v) {
		Any a = Any.toStruObjModel(v);
		int delta = idx - members.size();
		if (delta < 0) {
			members.set(idx, a);
		} else {
			for (int i = 0; i < delta; i++) { // create missing slots
				members.add(null);
			}
			members.add(a);
		}
		return v;
	}

	@Override
	public Object fetch(int idx) {
		Any a = fetchAny(idx);
		return a.toJavaObjModel();
	}

	@Override
	public Any fetchAny(int idx) {
		Any a = idx < members.size() ? members.get(idx) : null;
		return a == null ? Val.NULL : a;
	}
	

	@Override
	public Object del(int idx) {
		Any removed = idx < members.size() ? members.remove(idx) : null;
		return removed == null ? null : removed.toJavaObjModel();
	}

	@Override
	public <T> T push(T v) {
		pushAny(Any.toStruObjModel(v));
		return v;
	}

	@Override
	public <T extends Any> T pushAny(T any) {
		members.add(any);
		return any;
	}

	@Override
	public int len() {
		return members.size();
	}
	/**
	 * 
	 * @param arr
	 * @return true if all members are {@link Value#nullSafeEquals(Object, Object)} in the same iteration order
	 */
	@Override
	public boolean equals(Object arr) {
		if(!(arr instanceof Arr)) {
			return false;
		}
		Arr other = (Arr)arr;
		if(other.len() != size()) {
			return false;
		}
		Iterator<? extends Any> otherItr = ((ArrImpl)other).members().iterator();
		Iterator<? extends Any> thisItr = members().iterator();
		while(thisItr.hasNext()/* && otherItr.hasNext() see size comparison above*/) {
			if(! Value.nullSafeEquals(thisItr.next(), otherItr.next())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Arr copy() {
		ArrayList<Any> l = new ArrayList<>(size());
		for(Any el : members) {
			l.add(el.copy());
		}
		return new ArrImpl(l);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result += prime * size();
		if(members.size() > 0) {
			result += members.get(0).hashCode();
		}
		return result;
	}

	@Override
	public JsonType getType() {
		return JsonType.arr;
	}

	@Override
	public Arr reset() {
		members.clear();
		return this;
	}

	@Override
	public void sendTo(JixHandler sink) {
		sink.startArr();
		for(Any member : members) {
			member.sendTo(sink);
		}
		sink.endArr();
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

	@Override
	public ListIterator<Object> listIterator() {
		final ListIterator<Any> i = members.listIterator();
		return new ListIterator<Object>() {
			@Override
			public boolean hasNext() {
				
				return i.hasNext();
			}
			@Override
			public Object next() {
				final Any e = i.next();
				return e.toJavaObjModel();
			}
			@Override
			public boolean hasPrevious() {
				return i.hasPrevious();
			}
			@Override
			public Object previous() {
				final Any e = i.previous();
				return e.toJavaObjModel();
			}
			@Override
			public int nextIndex() {
				return i.nextIndex();
			}
			@Override
			public int previousIndex() {
				return i.previousIndex();
			}
			@Override
			public void remove() {
				i.remove();
			}
			@Override
			public void set(Object e) {
				Any a = Any.toStruObjModel(e);
				i.set(a);
			}
			@Override
			public void add(Object e) {
				Any a = Any.toStruObjModel(e);
				i.add(a);
			}
		};

	}

}
