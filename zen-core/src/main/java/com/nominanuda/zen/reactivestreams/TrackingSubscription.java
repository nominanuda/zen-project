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

import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Up to the {ususally @link Publisher} or who creates it (@see {@link InterceptingSubscription}) 
 * to call {@link #unrequest()} before calling {@link Subscriber#onNext(Object)} otherwise tracked
 * demand keeps growing
 */
public interface TrackingSubscription extends Subscription {

	/**
	 * 
	 * @return current demand, even 0
	 */
	long peekDemand();

	/**
	 * if demand is > 0 returns it. Otherwise waits for demand to become available (> 0)
	 * @return the availabe demand always > 0
	 * @throws InterruptedException if wait is interrupted
	 */
	long awaitDemand() throws InterruptedException;

	/**
	 *  if demand is > 0 returns it. Otherwise waits up to the set timeout for demand to become available (> 0)
	 * @param timeout
	 * @param unit
	 * @return the availabe demand always > 0
	 * @throws InterruptedException if wait is interrupted
	 */
	long awaitDemand(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 
	 * @return <code>true</code> if {@link Subscription} is still valid and demand was decreased
	 * if demand was already 0 it returns <code>false</code>
	 */
	boolean unrequest();

	/**
	 * @return true if not canceled
	 */
	boolean isValid();

	/**
	 * @return true if canceled
	 */
	boolean isCanceled();
}
