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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * it is up to the user not to signal {@link #next(Object)}, {@link #complete()} 
 * and {@link #error(Throwable)} concurrently. Can be used in different contexts
 * among which also for adapting classical blocking push pipelines. The only blocking
 * method is {@link #next(Object)}
 *
 * @param <T>
 */
public class BlockingPublisher<T> implements Publisher<T> {
	private Subscriber<? super T> subscriber;
	private TrackingSubscription subscription;

	@Override
	public void subscribe(Subscriber<? super T> s) {
		subscriber = s;
		subscription = new SubscriptionImpl();
		subscriber.onSubscribe(subscription);
	}

	/**
	 * blocking
	 * @param t
	 * @throws InterruptedException
	 */
	public void next(T t) throws RuntimeException {
		try {
			subscription.awaitDemand();
			subscription.unrequest();
			subscriber.onNext(t);
		} catch (InterruptedException e) {
			error(e);
			throw new RuntimeException(e);
		}
	}

	public void complete() {
		subscriber.onComplete();
	}

	public void error(Throwable t) {
		subscriber.onError(t);
	}

	public long getUnmetDemand() {
		return subscription.peekDemand();
	}

	public boolean wouldBlock() {
		return subscription.peekDemand() == 0;
	}

}
