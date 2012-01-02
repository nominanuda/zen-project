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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

import org.junit.Test;

import static org.junit.Assert.*;

public class SetListTest {

	@Test
	public void test1() {
		SetList<Integer> l = new SetList<Integer>();
		l.add(1);
		l.add(2);
		l.add(1);
		assertEquals(2, l.size());
		assertEquals(new Integer(1), l.get(0));
		assertEquals(new Integer(2), l.get(1));
		try {
			l.get(2);
			fail();
		} catch(NoSuchElementException e) {
		} catch(IndexOutOfBoundsException e) {
		}
		
	}

	@Test
	public void testLhs() {
		LinkedHashSet<Integer> l = new LinkedHashSet<Integer>();
		l.add(0);
		l.add(1);
		l.add(2);
		Iterator<Integer> it = l.iterator();
		it.next();
		it.next();
		it.remove();

		assertEquals(2, l.size());
		assertEquals(new Integer(0), l.iterator().next());
		Iterator<Integer> i = l.iterator();
		i.next();
		assertEquals(new Integer(2), i.next());
	}

}
