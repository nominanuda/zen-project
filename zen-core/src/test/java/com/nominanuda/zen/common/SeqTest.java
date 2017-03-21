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
package com.nominanuda.zen.common;

import static com.nominanuda.zen.seq.Seq.SEQ;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.junit.Test;

public class SeqTest {

	@Test
	public void testCopy() {
		LinkedList<String> l = new LinkedList<>();
		l.add("foo");
		l.add("bar");
		LinkedList<String> l1 = SEQ.copy(l);
		assertArrayEquals(l.toArray(), l1.toArray());
	}

	@Test
	public void testCopyRev() {
		LinkedHashSet<String> l = new LinkedHashSet<>();
		l.add("foo");
		l.add("bar");
		LinkedHashSet<String> l1 = SEQ.copyAndReverse(l);
		Iterator<String> i = l1.iterator();
		assertEquals("bar", i.next());
		assertEquals("foo", i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void testCopySet() {
		LinkedHashSet<String> l = new LinkedHashSet<>();
		l.add("foo");
		l.add("bar");
		LinkedHashSet<String> l1 = SEQ.copy(l);
		Iterator<String> i = l1.iterator();
		assertEquals("foo", i.next());
		assertEquals("bar", i.next());
		assertFalse(i.hasNext());
	}

}
