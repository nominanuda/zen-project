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

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import static com.nominanuda.lang.Collections.*;
import static org.junit.Assert.*;

public class CollectionsTest {

	@Test
	public void testSet() {
		HashSet<Integer> hs = hashSet(1,2,3);
		assertEquals(3, hs.size());
		Set<Object> lhs = buildSet(LinkedHashSet.class, "", 1, new Date());
		assertEquals(3, lhs.size());
		@SuppressWarnings("unused")
		Set<String> ss = buildSet(LinkedHashSet.class,"");
		assertEquals(0, buildSet(LinkedHashSet.class).size());
		@SuppressWarnings("unused")
		Set<Integer> hs2 = buildSet(LinkedHashSet.class, 1);

	}
//	public static <T> Set<T> buildSet(Class<? extends Set> sclass, T...ts) {
//		Set<T> s;
//		try {
//			s = (Set<T>)sclass.newInstance();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return s;
//	}

}
