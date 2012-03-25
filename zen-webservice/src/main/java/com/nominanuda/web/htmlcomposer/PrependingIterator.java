package com.nominanuda.web.htmlcomposer;

import java.util.Iterator;

import com.nominanuda.lang.Check;

public class PrependingIterator<T> implements Iterator<T> {//TODO
	private Iterator<T> delegate;
	private T firstElement;
	private boolean firstElementConsumed = false;

	public PrependingIterator(T firstelement, Iterator<T> delegate) {
		this.delegate = delegate;
		this.firstElement = firstelement;
	}

	public boolean hasNext() {
		return !firstElementConsumed || delegate.hasNext();
	}

	public T next() {
		if(!firstElementConsumed) {
			firstElementConsumed = true;
			return firstElement;
		} else if(delegate.hasNext()) {
			return delegate.next();
		} else {
			throw new IllegalStateException("iterator consumed");
		}
	}

	public void remove() {
		Check.illegalstate.fail();
	}
}