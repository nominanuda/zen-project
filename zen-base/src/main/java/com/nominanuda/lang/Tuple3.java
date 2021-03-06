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
package com.nominanuda.lang;

import com.nominanuda.code.Immutable;
import com.nominanuda.code.ThreadSafe;

@Immutable @ThreadSafe
public class Tuple3<T0, T1, T2> {
	private final T0 first;
	private final T1 second;
	private final T2 third;

	public Tuple3(T0 one, T1 two, T2 three) {
		first = one;
		second = two;
		third = three;
	}

	public T0 get0() {
		return first;
	}

	public T1 get1() {
		return second;
	}

	public T2 get2() {
		return third;
	}
}