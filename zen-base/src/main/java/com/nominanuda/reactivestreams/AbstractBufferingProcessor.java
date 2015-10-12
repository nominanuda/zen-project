package com.nominanuda.reactivestreams;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscription;

public abstract class AbstractBufferingProcessor<T, R> extends AbstractBufferingPublisher<R> implements Processor<T, R> {

	//override if you want manage demand and avoid buffering
	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public void onError(Throwable t) {
		errorInternal(t);
	}

	@Override
	public void onComplete() {
		completeInternal();
	}

}
