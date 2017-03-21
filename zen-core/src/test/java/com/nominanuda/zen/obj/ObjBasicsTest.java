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
package com.nominanuda.zen.obj;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import com.nominanuda.zen.obj.Obj;

import static com.nominanuda.zen.obj.SimpleJixParser.*;
import static com.nominanuda.zen.obj.Stru.*;

public class ObjBasicsTest {

	/*
{}.push(x, null) -> {x:null}
{}.push(x, 1) -> {x:1}
{}.push(x, {}) -> {x:{}}
{}.push(x, []) -> {}
{}.push(x, [[]]) -> {x:[]}
{}.push(x, [[1]]) -> {x:[1]}
{}.push(x, [1]) -> {x:[1]}
{x:{}}.push(x, null) -> {x:[{},null]}
{x:{y:1}}.push(x, 1) -> {x:[1,{y:1}]}
{x:{y:1}}.push(x, {y:2}) -> {x: [{y:2},{y:1}]}
{x:{y:1}}.push(x, []) -> {x:{y:1}}
{x:{y:1}}.push(x, [1,[]]) -> {x: [{y:1},1],{},[]}
	 */
	@Test
	public void testPush() {
		Obj o = Obj.make();
		assertEquals(1, o.push("foo", "bar"));
		//
		verify("{}",        "null",   "{x:null}");
		verify("{}",        "1",      "{x:1}");
		verify("{}",        "{}",     "{x:{}}");
		verify("{}",        "[]",     "{}");
		verify("{}",        "[[]]",   "{x:[]}");
		verify("{}",        "[[1]]",  "{x:[1]}");
		verify("{}",        "[1]",    "{x:1}");
		verify("{x:{}}",    "null",   "{x:[{},null]}");
		verify("{x:{y:1}}", "1",      "{x:[{y:1}, 1]}");
		verify("{x:{y:1}}", "{y:2}",  "{x: [{y:1},{y:2}]}");
		verify("{x:{y:1}}", "[]",     "{x:{y:1}}");
		verify("{x:{y:1}}", "[1,[]]", "{x: [{y:1},1,[]]}");
	}
	String x = "x";
	private void verify(String before, String pushedVal, String result) {
		Obj o = obj(before);
		o.push(x, parse(pushedVal));
		assertEquals(obj(result), o);
	}
//	private static Obj j(String str) {
//		return obj(str);
//	}
//	private static Object jj(String str) {
//		return Builder.simple(str);
//	}

	@Test
	public void testCreate() {
		assertNotNull(Obj.make());
		assertNotNull(Arr.make());
		assertNotNull(Obj.make().newObj());
		assertNotSame(Obj.make(), Obj.make());
		assertNotSame(Obj.make(), Obj.make().newObj());
		
		Obj o = Obj.make();
		o.store("foo", "bar");
		assertEquals("bar", o.fetch("foo"));
	}

	@Test
	public void testHashCode() {
		HashSet<Obj> s = new HashSet<>();
		s.add(Obj.make());
		assertEquals(1, s.size());
		s.add(Obj.make());
		assertEquals(1, s.size());
	}

	@Test
	public void testCopy() {
		Obj o = Obj.make();
		o.store("foo", "bar");
		assertEquals(o, o);
		assertEquals(o, o.copy());
		assertNotSame(o, o.copy());
		assertEquals("bar", o.copy().fetch("foo"));
	}

	@Test
	public void testCopyCast() {
		Obj o = Obj.make();
		o.store("foo", "bar");
		Obj copied = o.copyCast();
		assertEquals(o, copied);
		try {
			String x = o.copyCast();
			fail(x);
		} catch(ClassCastException ignore) {}
	}

	@Test
	public void testVarargs() {
		assertTrue(Obj.make().isEmpty());
		try {
			Obj.make("");
			fail();
		} catch(IllegalArgumentException e) {}
		try {
			Obj.make(null, null);
			fail();
		} catch(NullPointerException e) {}
		Obj.make("", null);
		assertNull(Arr.make((Object)null).iterator().next());
		assertTrue(Arr.make().isEmpty());
		assertTrue(Arr.make((Object[])null).isEmpty());
	}

}
