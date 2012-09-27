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
package com.nominanuda.dataobject;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

public class HashCodeEqualsTest {

	@Test
	public void test1() {
		HashSet<DataStruct> s = new HashSet<DataStruct>();
		DataObject o1 = STRUCT.newObject();
		s.add(o1);
		assertEquals(1, s.size());
		DataObject o2 = STRUCT.newObject();
		s.add(o2);
		assertEquals(1, s.size());
		
	}

	@Test
	public void test2() {
		HashSet<DataStruct> s = new HashSet<DataStruct>();
		DataObject o1 = STRUCT.newObject().with("a", 1);
		s.add(o1);
		assertEquals(1, s.size());
		DataObject o2 = STRUCT.newObject();
		s.add(o2);
		assertEquals(2, s.size());
		
	}

	@Test
	public void test3() {
		HashSet<DataStruct> s = new HashSet<DataStruct>();
		DataObject o1 = STRUCT.newObject();
		s.add(o1);
		assertEquals(1, s.size());
		o1.with("a", 1);
		assertEquals(1, s.size());
		assertSame(o1, s.iterator().next());
	}

	@Test
	public void test4() {
		HashSet<DataStruct> s = new HashSet<DataStruct>();
		DataObject o1 = STRUCT.newObject().with("a", 1);
		s.add(o1);
		DataObject o2 = STRUCT.newObject();
		s.add(o2);
		assertTrue(s.contains(o2));
		assertEquals(2, s.size());
		assertFalse(o1.equals(o2));
		int h21 = o2.hashCode();
		o2.with("a", 1);
		int h22 = o2.hashCode();
		assertTrue(h21 != h22);
		assertTrue(s.contains(o2));
		assertEquals(o1, o2);
		assertEquals(2, s.size());
	}
}
