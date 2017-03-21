/*
 * Copyright 2008-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.zen.reactivestreams;

import java.util.function.Function;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Bridges the downstream {@link Subscription} events upstream and the {@link Subscriber#onNext(Object)}
 * events are transformed and forwarded on the calling {@link Thread}
 *
 * @param <T>
 * @param <R>
 */
public class SyncProcessor<T, R> extends TransformingProcessor<T, R> {
	private Subscriber<? super R> subscriber;
	private DelegatingSubscription subscription = new DelegatingSubscription();

	public SyncProcessor(Function<T, R> fn) {
		setFunction(fn);
	}

	public SyncProcessor() {
	}

	@Override
	public final void onSubscribe(Subscription s) {
		subscription.setSubscription(s);
	}

	@Override
	public final void onNext(T t) {
		try {
			in(t);
		} catch(Exception e) {
			subscription.cancel();
			onError(e);
		}
	}

	@Override
	protected void out(R r) {
		subscriber.onNext(r);
	}

	@Override
	public final void onError(Throwable t) {
		subscriber.onError(t);
	}

	@Override
	public final void onComplete() {
		subscriber.onComplete();
	}

	@Override
	public final void subscribe(Subscriber<? super R> s) {
		this.subscriber = s;
		this.subscriber.onSubscribe(subscription);
	}

}
