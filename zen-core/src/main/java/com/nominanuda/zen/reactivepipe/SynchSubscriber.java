package com.nominanuda.zen.reactivepipe;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public interface SynchSubscriber<T> extends Subscriber<T> {

	@Override
	default public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}
}
