package com.nominanuda.zen.reactivepipe;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;

public class SyncProcessor<T,R> implements SynchSubscriber<T>, Processor<T,R> {

	@Override
	public void onNext(T t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribe(Subscriber<? super R> s) {
		// TODO Auto-generated method stub
		
	}

}
