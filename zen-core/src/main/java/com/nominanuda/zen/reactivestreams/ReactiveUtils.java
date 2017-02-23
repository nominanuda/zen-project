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

import static java.lang.Long.MAX_VALUE;

public class ReactiveUtils {

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return the sum or {@link Long#MAX_VALUE} to prevent overflow
	 */
	public static long cappedSum(long l1, long l2) {
		return MAX_VALUE - l1 > l2 ? l1 + l2 : MAX_VALUE;
	}
}
