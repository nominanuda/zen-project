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

import org.junit.Test;

import com.nominanuda.zen.reactivestreams.DevNull;

public class StreamTest {

	@Test
	public void testSync() {
		BlockingPublisher<String> p = new BlockingPublisher<String>();
		DevNull<String> devNull = new DevNull<String>();
		p.subscribe(devNull);
		for(int i = 0; i < 100; i++) {
			p.next("");
		}
		p.complete();
		assertEquals(100L, devNull.getEventCount());
		assertEquals(1L, devNull.getCompleteCount());
		assertEquals(0L, devNull.getErrorCount());
	}

//	@Test
//	public void testDevNullError() {
//		RunnableBufferingPublisher<String> p = new RunnableBufferingPublisher<String>(){
//			protected void work() {
//				for(int i = 0; i < 100; i++) {
//					publishInternal("");
//				}
//				throw new RuntimeException("miki was here");
//			}};
//		DevNull<String> devNull = new DevNull<String>();
//		p.subscribe(devNull);
//		
//		try {
//			p.run();
//			fail();
//		} catch (Exception e) {
//		}
//
//		assertEquals(100L, devNull.getEventCount());
//		assertEquals(0L, devNull.getCompleteCount());
//		assertEquals(1L, devNull.getErrorCount());
//		assertEquals("miki was here", devNull.getLastError().getMessage());
//	}

}
