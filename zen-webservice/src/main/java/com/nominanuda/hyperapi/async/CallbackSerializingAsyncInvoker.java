/*
 * Copyright 2008-2011 the original author or authors.
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
package com.nominanuda.hyperapi.async;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CallbackSerializingAsyncInvoker extends AsyncInvokerImpl {
	private Queue<Callable<Void>> pendingCallbacks = new ConcurrentLinkedQueue<Callable<Void>>();

	@Override
	protected <T> void invokeSuccessCb(final T result, final SuccessCallback<T> ss) {
		pendingCallbacks.add(new Callable<Void>() {
			public Void call() throws Exception {
				ss.apply(result, "textStatus", null);
				return null;
			}
		});
	}

	@Override
	protected void invokeErrorCb(final ErrorCallback ee, final Exception e) {
		pendingCallbacks.add(new Callable<Void>() {
			public Void call() throws Exception {
				ee.apply(null, "textStatus", e);
				return null;
			}
		});
	}

	@Override
	public void await(long timeout, TimeUnit unit) throws Exception {
		long start = System.currentTimeMillis();
		long millis = TimeUnit.MILLISECONDS.convert(timeout, unit);
		int numPendingTasks = getFutures().size();
		while(numPendingTasks > 0) {
			if(System.currentTimeMillis() - start > millis) {
				throw new TimeoutException();
			}
			Callable<Void> cb = null;
			while((cb = pendingCallbacks.poll()) != null) {
				numPendingTasks--;
				try {
					cb.call();
				} catch (Exception e) {
				}
			}
		}
	}
}
