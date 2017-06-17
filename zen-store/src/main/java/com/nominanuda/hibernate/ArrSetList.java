package com.nominanuda.hibernate;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.ArrImpl;

//TODO avoid duplicates
public class ArrSetList extends ArrImpl implements Set<Object>, UserCollectionType {

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

	@Override
	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister)
			throws HibernateException {
		return new HibTArr(session);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
		HibTArr htarr = new HibTArr(session);
		htarr.addAll((Collection)collection);
		return htarr;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator<?> getElementsIterator(Object collection) {
		return ((Iterable)collection).iterator();	}

	@Override
	public boolean contains(Object collection, Object entity) {
		return ((HibTArr)collection).contains(entity);
	}

	@Override
	public Object indexOf(Object collection, Object entity) {
		return ((HibTArr)collection).indexOf(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner,
			Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
		Collection originalColl = (Collection)original;
		Collection targetColl = (Collection)target;
		targetColl.clear();
		targetColl.addAll(originalColl);
		return targetColl;
	}

	@Override
	public Object instantiate(int anticipatedSize) {
		return Arr.make();//new HibTArr();
	}



}
