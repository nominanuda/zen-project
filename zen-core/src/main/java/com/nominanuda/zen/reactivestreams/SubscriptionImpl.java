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
import static java.lang.Long.MAX_VALUE;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SubscriptionImpl implements TrackingSubscription {
	private volatile long outstanding = 0;
	private volatile boolean valid = true;
	private CountDownLatch latch = new CountDownLatch(1);

	@Override
	public void request(long n) {
		if(valid) {
			if(n < 0) {
				//TODO onError(new IllegalArgumentException("TODO rule n..."));
			} else if(n > 0){
				boolean doRelease = outstanding == 0;
				outstanding = cappedSum(outstanding, n);
				if(doRelease) {
					gateOpen();
				}
			}
		}
	}
	@Override
	public void cancel() {
		if(valid) {
			valid = false;
		}
	}
	@Override
	public long peekDemand() {
		return outstanding;
	}

	@Override
	public long awaitDemand() throws InterruptedException {
		if(outstanding == 0) {
			latch.await();
		}
		return outstanding;
	}

	@Override
	public long awaitDemand(long timeout, TimeUnit unit) throws InterruptedException {
		if(outstanding == 0) {
			latch.await(timeout, unit);
		}
		return outstanding;
	}

	private void gateClose() {
		latch = new CountDownLatch(1);
	}
	private void gateOpen() {
		latch.countDown();
	}

	@Override
	public boolean unrequest() {
		if(valid) {
			if(outstanding == 0) {
				return false;
			} else if(outstanding < MAX_VALUE) {
				if(--outstanding == 0) {
					gateClose();
				}
				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public boolean isCanceled() {
		return ! valid;
	}

}
