package com.nominanuda.reactivestreams;

import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DevNull<T> implements Subscriber<T> {
	private AtomicLong eventCount = new AtomicLong(0);
	private AtomicLong completeCount = new AtomicLong(0);
	private AtomicLong errorCount = new AtomicLong(0);
	private Exception lastError;
	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public void onNext(T t) {
		eventCount.incrementAndGet();
	}

	@Override
	public void onError(Throwable t) {
		lastError = (Exception)t;
		errorCount.incrementAndGet();
	}

	@Override
	public void onComplete() {
		completeCount.incrementAndGet();
	}

	public long getEventCount() {
		return eventCount.get();
	}

	public long getCompleteCount() {
		return completeCount.get();
	}

	public long getErrorCount() {
		return errorCount.get();
	}

	public Exception getLastError() {
		return lastError;
	}

}
