package com.nominanuda.web.htmlcomposer;

import java.util.Iterator;

import com.nominanuda.lang.Check;

public class AppendingIterator<T> implements Iterator<T> {
	private Iterator<T> delegate;
	private T lastElement;
	private boolean lastElementConsumed = false;

	public AppendingIterator(Iterator<T> delegate, T lastelement) {
		this.delegate = delegate;
		this.lastElement = lastelement;
	}

	public boolean hasNext() {
		boolean b = delegate.hasNext();
		return b || !lastElementConsumed;
	}

	public T next() {
		if(delegate.hasNext()) {
			return delegate.next();
		} else if(!lastElementConsumed) {
			lastElementConsumed = true;
			return lastElement;
		} else {
			throw new IllegalStateException("iterator consumed");
		}
	}

	public void remove() {
		Check.illegalstate.fail();
	}
}