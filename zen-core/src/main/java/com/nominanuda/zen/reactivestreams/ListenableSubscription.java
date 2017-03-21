package com.nominanuda.zen.reactivestreams;

import java.util.function.Consumer;

import org.reactivestreams.Subscription;

public interface ListenableSubscription extends Subscription {

	public void onCancel(Runnable cb);

	/**
	 * It is very important that the supplied {@link Consumer} does not call
	 * {@link Subscriber#onNext(Object)} on the same {@link Thread} see Reactive
	 * Streams specification rule 3.3. Still this capability is exposed for
	 * other uses that can be handy.
	 * 
	 * @param cb
	 */
	public void onRequest(Consumer<Long> cb);
}
