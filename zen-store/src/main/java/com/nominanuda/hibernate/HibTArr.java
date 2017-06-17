package com.nominanuda.hibernate;

import java.util.List;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.TArr;

@SuppressWarnings("rawtypes")
public class HibTArr extends PersistentList implements TArr {
	private static final long serialVersionUID = 6669958680391933385L;

	public HibTArr() {
		super();
	}

	public HibTArr(SharedSessionContractImplementor session, List list) {
		super(session, list);
		// TODO Auto-generated constructor stub
	}

	public HibTArr(SharedSessionContractImplementor session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Obj newObj() {
		return Obj.make();
	}

	@Override
	public Arr newArr() {
		return Arr.make();
	}

	@Override
	public Object store(int idx, Object v) {
		return set(idx, v);
	}

	@Override
	public Object fetch(int idx) {
		return get(idx);
	}

	@Override
	public Object del(int idx) {
		return remove(idx);
	}

	@Override
	public int len() {
		return size();
	}

	@Override
	public Object push(Object v) {
		add(v);
		return v;
	}

	@Override
	public TArr reset() {
		clear();
		return this;
	}

}
