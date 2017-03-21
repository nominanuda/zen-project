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
package com.nominanuda.zen.common;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

@Immutable @ThreadSafe
public class Tuple2<T0, T1> {
	private final T0 first;
	private final T1 second;

	public Tuple2(T0 one, T1 two) {
		first = one;
		second = two;
	}

	public T0 get0() {
		return first;
	}

	public T1 get1() {
		return second;
	}
}