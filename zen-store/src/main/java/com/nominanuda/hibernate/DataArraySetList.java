package com.nominanuda.hibernate;

import java.util.Collection;
import java.util.Set;
import java.util.Spliterator;

import com.nominanuda.zen.obj.ArrImpl;

//TODO avoid duplicates
public class DataArraySetList extends ArrImpl implements Set<Object> {

	@Override
	public Spliterator<Object> spliterator() {
		return super.spliterator();
	}

	@Override
	public int size() {
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return super.contains(o);
	}

	@Override
	public Object[] toArray() {
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
	}

	@Override
	public boolean add(Object e) {
		return super.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return super.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		return super.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return super.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return super.removeAll(c);
	}

	@Override
	public void clear() {
		super.clear();
	}



}
