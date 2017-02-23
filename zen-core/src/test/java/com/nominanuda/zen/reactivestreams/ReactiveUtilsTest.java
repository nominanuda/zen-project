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
import static org.junit.Assert.*;

import org.junit.Test;

public class ReactiveUtilsTest {

	@Test
	public void test() {
		long l1 = MAX_VALUE;
		long l2 = MAX_VALUE;
		long l3 = MAX_VALUE - 2;
		assertEquals(MAX_VALUE, cappedSum(l1, l2));
		assertEquals(MAX_VALUE, cappedSum(l1, l3));
		assertEquals(MAX_VALUE, cappedSum(3, l3));
		assertEquals(MAX_VALUE, cappedSum(2, l3));
		assertEquals(MAX_VALUE - 1, cappedSum(1, l3));
		assertEquals(MAX_VALUE - 2, cappedSum(0, l3));
		assertEquals(2, cappedSum(1, 1));
	}

}
