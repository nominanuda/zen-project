package com.nominanuda.reactivestreams;

import static com.nominanuda.lang.Check.notNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;



public abstract class AbstractBufferingPublisher<T> implements DemandInjectablePublisher<T> {
	private Demand subscription = new Demand();
	private final Queue<T> q = new ConcurrentLinkedQueue<T>();
	private Subscriber<? super T> subscriber;
	
	private boolean outputCompleted = false;

	protected void onBeforeSubscribe() {
	}

	protected void onAfterSubscribe() {
	}
	
	protected void onAfterCancel() {
	}

	@Override
	public void setSubscription(Demand sImpl) {
		this.subscription = sImpl;
	}
	/**
	 * @throws {@link RuntimeException} wrapping {@link #onAfterSubscribe()} generated {@link Exception}
	 * if {@link #onBeforeSubscribe()} throws an {@link Exception}, it is rethrown as is and {@link Subscriber#onSubscribe(Subscription)}
	 * never called
	 */
	@Override
	public final void subscribe(Subscriber<? super T> subscriber) throws RuntimeException {
		onBeforeSubscribe();
		if(subscription == null) {
			subscription = new Demand();
		}
		notNull(this.subscriber = subscriber).onSubscribe(subscription);
		try {
			onAfterSubscribe();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected final void publishInternal(T t) {
		if(! outputCompleted) {
			flushOrQueue(t);
		}
	}

	protected final void errorInternal(Throwable e) {
		if(! outputCompleted) {
			outputCompleted = true;
			q.clear();
			subscriber.onError(e);
		}
	}
	protected final void completeInternal() {
		if(! outputCompleted) {
			outputCompleted = true;
			flush();
			if(q.size() > 0) {
				subscription.onDemandIncrease(new Runnable() {
					public void run() {
						flush();
					}
				});
			}
		}
	}


	private final void flushOrQueue(T b) {
		if(subscription.unrequest()) {
			subscriber.onNext(b);
			flush();
		} else {
			q.add(b);
		}
	}

	private final void flush() {
		long howMany = Math.min(subscription.getDemand(), q.size());
		for(int i = 0; i < howMany; i++) {
			subscription.unrequest();
			subscriber.onNext(q.poll());
		}
		if(q.size() == 0 && outputCompleted) {
			subscriber.onComplete();
		}
	}

}
