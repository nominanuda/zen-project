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

import static com.nominanuda.zen.reactivestreams.ReactiveUtils.cappedSum;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.nominanuda.zen.concurrent.SynchExecutor;

public class DelegatingSubscription implements ListenableSubscription {
	private volatile boolean canceled = false;
	private volatile long unrecordedDemand = 0;
	private final CopyOnWriteArrayList<Runnable> cancelCallbacks = new CopyOnWriteArrayList<Runnable>();
	private Executor callbackExecutor = new SynchExecutor();
	private Subscription delegee;

	@Override
	public void cancel() {
		if(! canceled) {
			canceled = true;
			if(delegee != null) {
				delegee.cancel();
			}
			for(Runnable onCancel : cancelCallbacks ) {
				callbackExecutor.execute(onCancel);
			}
		}
	}

	public void setSubscription(Subscription s) {
		this.delegee = s;
		if(canceled) {
			this.delegee.cancel();
		} else if(unrecordedDemand > 0) {
			this.delegee.request(unrecordedDemand);
		}
	}

	public void setCallbackExecutor(Executor callbackExecutor) {
		this.callbackExecutor = callbackExecutor;
	}

	@Override
	public void onCancel(Runnable cb) {
		cancelCallbacks.add(cb);
	}

	@Override
	public void request(long n) {
		if(! canceled) {
			if(delegee != null) {
				delegee.request(n);
			} else {
				unrecordedDemand = cappedSum(unrecordedDemand, n);
			}
			//trick to avoid creation of new Runnables
			//assumes that request() is not signaled concurrently
			this.n = n;
			callbackExecutor.execute(onRequestCallbacks);
		}
	}
/*
	the two lines above, together with the following allow for request() callbacks;
	 the problem is how to comply with rule 3.3 
	Subscription.request MUST place an upper bound on possible synchronous recursion between Publisher and Subscriber
	 An example for undesirable synchronous, open recursion would be 
	Subscriber.onNext -> Subscription.request -> Subscriber.onNext -> ..., 
	as it very quickly would result in blowing the calling Thread´s stack.
*/

	private final CopyOnWriteArrayList<Consumer<Long>> requestCallbacks = new CopyOnWriteArrayList<Consumer<Long>>();

	private long n;
	private Runnable onRequestCallbacks = () -> {
		for(Consumer<Long> onRequest : requestCallbacks) {
			callbackExecutor.execute(() -> onRequest.accept(n));
		}
	};

	/**
	 * It is very important that the supplied {@link Consumer} does not call {@link Subscriber#onNext(Object)}
	 * on the same {@link Thread}  see Reactive Streams specification rule 3.3. 
	 * Still this capability is exposed for other uses that can be handy. 
	 * @param cb
	 */
	@Override
	public void onRequest(Consumer<Long> cb) {
		requestCallbacks.add(cb);
	}
}
