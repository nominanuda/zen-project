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

import static com.nominanuda.zen.common.Maths.MATHS;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.nominanuda.zen.concurrent.Exec;
//@Ignore
public class BufferingProcessorTest {

	
	@Test
	public void test1() throws InterruptedException {
		BufferingProcessor<Integer,String> p = new BufferingProcessor<Integer,String>();
		p.setFunction(i -> {
			//Exec.sleep(1000);
			return i.toString();
		});
		//p.setWorkerPool(Executors.newFixedThreadPool(10));
		BlockingPublisher<Integer> counter = new BlockingPublisher<Integer>();
		counter.subscribe(p);
		Accumulator<String, String> concat = concat();
		p.subscribe(concat);
		for(int i = 0; i < 3; i++) {
			counter.next(i);
		}
		counter.complete();
		//Exec.sleep(3300);
		assertEquals("012", concat.get());
 	}
//TODO stress limits in demand
	@Test
	public void test2() throws InterruptedException {
		long loops = 100L;
		long avgBlockingSleep = 50;
		int nThreads = 10;
		for(int i = 0; i < 10; i++) {
			System.err.println(".");
			_test2(loops, avgBlockingSleep, nThreads);
		}
	}
	private void _test2(long loops, long avgBlockingSleep, int nThreads) throws InterruptedException {
		AtomicBoolean failed = new AtomicBoolean(false);
		final CountDownLatch cdl = new CountDownLatch(1+(int)loops);
		AtomicLong last = new AtomicLong(0);
		Accumulator<Long, Long> count = new Accumulator<Long, Long>() {
			volatile long sum = 0;
			public synchronized void onNext(Long t) {
				failed.compareAndSet(false, ! assertEquals2((long) last.getAndAdd(1L), (long)t));
				cdl.countDown();
				sum += 1;
			}
			public void onComplete() {
				setResult(sum);
				cdl.countDown();
			}
		};
		BufferingProcessor<Long, Long> p = new BufferingProcessor<Long, Long>();
		p.setBufferSize(32);
		p.setWorkerPool(newFixedThreadPool(nThreads));
		p.setFunction(i -> {
			Exec.sleep(MATHS.randLong(avgBlockingSleep));
			return i;
		});
		BlockingPublisher<Long> counter = new BlockingPublisher<Long>();
		counter.subscribe(p);
		p.subscribe(count);
		for(long i = 0; i < loops; i++) {
			counter.next(i);
		}
		counter.complete();
		assertTrue(cdl.await(avgBlockingSleep * loops, MILLISECONDS));
		assertFalse(failed.get());
		assertEquals(loops, (long)count.get());
 	}

	private boolean assertEquals2(long l1, long l2) {
//		assertEquals(l1, l2);
		if(l1 != l2) {
			System.err.println("!!!!!!!!!!! "+l1+" - "+l2);
			return false;
		} else {
			return true;
		}
	}

	Accumulator<String, String> concat() {
		return new Accumulator<String, String>() {
			StringBuffer sb = new StringBuffer();
			public void onNext(String t) {
				sb.append(t);
			}
			public void onComplete() {
				setResult(sb.toString());
			}
		};
	}
}
