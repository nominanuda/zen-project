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

import javax.annotation.Nullable;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class InterceptingSubscription<T> extends SubscriptionImpl implements Subscriber<T> {
	private @Nullable Subscription subscription;
	private final Subscriber<T> subscriber;

	public InterceptingSubscription(Subscriber<T> subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public void request(long n) {
		if(n < 0) {
			onError(new IllegalArgumentException("TODO rule n..."));
		} else if(isValid()) {
			super.request(n);
			if(subscription != null) {
				subscription.request(n);
			}
		}
	}

	@Override
	public void cancel() {
		if(isValid()) {
			super.cancel();
			if(subscription != null) {
				subscription.cancel();
			}
		}
	}

	/**
	 * it is up to the calling code to pass a valid subscription or <code>null</code>
	 * in both cases this class acts as a valid {@link Subscription}, since the real 
	 * {@link Subscriber} uses it for demand signaling. If a not <code>null</code> 
	 * {@link Subscription} is passed, all events are also forwarded and it can be used
	 * by the owning {@link Publisher}.
	 * @param s
	 */
	@Override
	public void onSubscribe(@Nullable Subscription s) {
		if(s != null) {
			try {
				setSubscription(s); 
			} catch(IllegalStateException e) {
				subscriber.onError(new IllegalArgumentException("already subscribed"));
				cancel();
				return;
			}
			subscriber.onSubscribe(this);
		}
	}

	public void setSubscription(Subscription s) throws IllegalStateException {
		if(subscription != null) {
			throw new IllegalStateException("Subscription already set");
		} else {
			subscription = s;
		}
	}

	@Override
	public void onNext(T t) {
		if(isValid()) {
			unrequest();
			subscriber.onNext(t);
		}
	}

	@Override
	public void onError(Throwable t) {
		cancel();
		subscriber.onError(t);
	}

	@Override
	public void onComplete() {
		cancel();
		subscriber.onComplete();
	}

}
