package com.nominanuda.zen.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class BlockingSink<T> implements Subscriber<T> {
	protected Subscription subscription;
	@Override
	public void onSubscribe(Subscription s) {
		subscription = s;
		subscription.request(1);
	}

	@Override
	public void onNext(T t) {
		try {
			blockingOp(t);
			subscription.request(1);
		} catch (Exception e) {
			subscription.cancel();
		}
	}

	protected abstract void blockingOp(T t) throws Exception;

//	@Override
//	public void onError(Throwable t) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onComplete() {
//		// TODO Auto-generated method stub
//		
//	}

}
