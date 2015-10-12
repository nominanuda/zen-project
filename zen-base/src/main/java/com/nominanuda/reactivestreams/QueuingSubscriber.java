package com.nominanuda.reactivestreams;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class QueuingSubscriber<T> implements Subscriber<T> {
	final protected List<T> events = Collections.synchronizedList(new LinkedList<T>());
	protected Subscription subscription;
	@Override
	public void onSubscribe(Subscription s) {
		this.subscription = s;
		this.subscription.request(Long.MAX_VALUE);
	}

	@Override
	public final void onNext(T t) {
		events.add(t);
	}

}
