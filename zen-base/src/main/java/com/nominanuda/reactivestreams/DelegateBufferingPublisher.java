package com.nominanuda.reactivestreams;

public class DelegateBufferingPublisher<T> extends AbstractBufferingPublisher<T> {

	public void publish(T t) {
		super.publishInternal(t);
	}

	public void error(Throwable e) {
		super.errorInternal(e);
	}
	public void complete() {
		super.completeInternal();
	}

}
