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
package com.nominanuda.springsoy;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nominanuda.springsoy.SoyViewResolver.SoyView.LongToInt;

public class LongToIntTest {

	@Test
	public void test() {
		LongToInt l2i = new LongToInt();
		HashMap<String, Object> m1 = new HashMap<String, Object>();
		m1.put("a1", 1L);
		HashMap<String, Object> m2 = new HashMap<String, Object>();
		m2.put("a2", 1L);
		LinkedList<Object> l = new LinkedList<Object>();
		l.add(m2);
		l.add(1L);
		m1.put("l", l);
		l2i.longToInt(m1);
		assertEquals(new Integer(1), m1.get("a1"));
		assertEquals(new Integer(1), ((List<?>)m1.get("l")).get(1));
		assertEquals(new Integer(1), ((Map<?,?>)((List<?>)m1.get("l")).get(0)).get("a2"));
	}

}
