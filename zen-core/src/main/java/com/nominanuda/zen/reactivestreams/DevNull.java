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
