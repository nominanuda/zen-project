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
		o2.with("a", 1);
		assertTrue(s.contains(o2));
		assertEquals(o1, o2);
		assertEquals(2, s.size());
	}
}
