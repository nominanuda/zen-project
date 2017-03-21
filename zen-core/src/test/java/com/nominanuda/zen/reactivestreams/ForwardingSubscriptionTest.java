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

import static org.junit.Assert.*;

import java.util.function.Function;

import org.junit.Test;

public class ForwardingSubscriptionTest {

	@Test
	public void test1() {
		DevNull<String> devNull = new DevNull<String>();
		SubscriptionImpl demand = new SubscriptionImpl();
		SyncProcessor<String, String> p = new SyncProcessor<String, String>(Function.identity());
		
		p.subscribe(devNull);
		p.onSubscribe(demand);

		assertEquals(Long.MAX_VALUE, demand.peekDemand());
		p.onNext("1");
		p.onNext("2");
		p.onNext("3");
		p.onComplete();
		assertEquals(3L, devNull.getEventCount());
		assertEquals(1L, devNull.getCompleteCount());
	}

	@Test
	public void test2() {
		DevNull<String> devNull = new DevNull<String>();
		SubscriptionImpl demand = new SubscriptionImpl();
		SyncProcessor<String, String> p = new SyncProcessor<String, String>(Function.identity());
		
		p.onSubscribe(demand);
		p.subscribe(devNull);

		assertEquals(Long.MAX_VALUE, demand.peekDemand());
		p.onNext("1");
		p.onNext("2");
		p.onNext("3");
		p.onComplete();
		assertEquals(3L, devNull.getEventCount());
		assertEquals(1L, devNull.getCompleteCount());
	}

}
