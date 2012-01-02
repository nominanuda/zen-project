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
public class Tuple<T0,T1,T2,T3,T4,T5,T6,T7,T8,T9> {
	private final Object[] members;

	public int getCardinality() {
		return members.length;
	}
	public Tuple(T0 o0) {
		members= new Object[] {o0};
	}
	public Tuple(T0 o0, T1 o1) {
		members= new Object[] {o0, o1};
	}

	public Tuple(T0 o0, T1 o1, T2 o2) {
		members= new Object[] {o0, o1, o2};
	}
	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3) {
		members= new Object[] {o0, o1, o2, o3};
	}
	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4) {
		members= new Object[] {o0, o1, o2, o3, o4};
	}
	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5) {
		members= new Object[] {o0, o1, o2, o3, o4, o5};
	}
	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5, T6 o6) {
		members= new Object[] {o0, o1, o2, o3, o4, o5, o6};
	}
	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5, T6 o6, T7 o7) {
		members= new Object[] {o0, o1, o2, o3, o4, o5, o6, o7};
	}
	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5, T6 o6, T7 o7, T8 o8) {
		members= new Object[] {o0, o1, o2, o3, o4, o5, o6, o7, o8};
	}

	public Tuple(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5, T6 o6, T7 o7, T8 o8, T9 o9) {
		members= new Object[] {o0, o1, o2, o3, o4, o5, o6, o7, o8, o9};
	}
	//zero based
	public Object get(int i) {
		return members[i];
	}
	@SuppressWarnings("unchecked")
	public T0 get0() {
		return (T0)get(0);
	}
	@SuppressWarnings("unchecked")
	public T1 get1() {
		return (T1)get(1);
	}
	@SuppressWarnings("unchecked")
	public T2 get2() {
		return (T2)get(2);
	}
	@SuppressWarnings("unchecked")
	public T3 get3() {
		return (T3)get(3);
	}
	@SuppressWarnings("unchecked")
	public T4 get4() {
		return (T4)get(4);
	}
	@SuppressWarnings("unchecked")
	public T5 get5() {
		return (T5)get(5);
	}
	@SuppressWarnings("unchecked")
	public T6 get6() {
		return (T6)get(6);
	}
	@SuppressWarnings("unchecked")
	public T7 get7() {
		return (T7)get(7);
	}
	@SuppressWarnings("unchecked")
	public T8 get8() {
		return (T8)get(8);
	}
	@SuppressWarnings("unchecked")
	public T9 get9() {
		return (T9)get(9);
	}
}