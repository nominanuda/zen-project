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

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DataStructHelperTest {

	@Test
	public void testToString() {
		assertEquals("{}", new DataObjectImpl().toString());
		assertEquals("[]", new DataArrayImpl().toString());
		Obj obj = new DataObjectImpl();
		Arr arr = new DataArrayImpl();
		arr.put(1, "\"");
		arr.add(true);
		arr.add("\\");
		arr.add(1.0);
		arr.add(1.1);
		arr.add(2L);
		obj.put("x", arr);
		obj.put("y", obj);
		assertEquals("{\"x\":[null,\"\\\"\",true,\"\\\\\",1,1.1,2]," +
			"\"y\":{\"x\":[null,\"\\\"\",true,\"\\\\\",1,1.1,2]}}", 
			obj.toString());
	}
	@Test
	public void testGetPathSafe() {
		Obj obj = new DataObjectImpl();
		obj.putNewObject("foo")
			.putNewArray("bar")
			.putNewObject(2)
			.putNewArray("baz")
			.put(1, 1);
		System.err.println(obj);
		assertNull(obj.getPathSafe("foo.bar.3.bazooka"));
		assertEquals(1, obj.getPathSafe("foo.bar.2.baz.1"));
	}

	@Test
	public void testEqualsAndClone() throws IOException, ParseException, SAXException {
		Obj obj = (Obj)new JSONParser().parse(
			"{\"a\":null,\"b\":{\"c\":1},\"d\":\"X\"}");
		Obj obj2 = new DataStructHelper().clone(obj);
		assertTrue(new DataStructHelper().equals(obj, obj2));
	}
	
	@Test
	public void testStringEscapeRoundTrip() throws IOException, ParseException, SAXException {
		String[] examples = new String[] {
			"", "\\", "\"", "ẽ", "è"
		};
		for(String s : examples) {
			String s1 = DataStructHelper.STRUCT.toJsonString(s);
			System.err.println(s1);
			String s2 = DataStructHelper.STRUCT.jsonStringUnescape(s1.substring(1, s1.length() - 1));
			assertEquals(s, s2);
		}
	}
	
	@Test
	public void shouldTranslateDataArrayWith2EqualsObjectIntoAListWith2EqualsMap() {
		Obj val1 = new DataObjectImpl();
		Obj val2 = new DataObjectImpl();
		Arr arr = new DataArrayImpl();
		arr.add(val1);
		arr.add(val2);
		List<?> list = new DataStructHelper().toMapsAndLists(arr);
		assertEquals(2, list.size());
		assertEquals(list.get(0), list.get(1));
	}

	@Test
	public void testSaObjSeq() {
		Arr a0 = STRUCT.newArray();
		Arr a1 = STRUCT.newArray().with(STRUCT.newObject());
		Arr a2 = STRUCT.newArray().with("");

		for(Obj o : STRUCT.asObjSeq(a0)) {
			assertFalse(o.exists("foo"));
		}
		for(Obj o : STRUCT.asObjSeq(a1)) {
			assertFalse(o.exists("foo"));
		}
		try {
			for(Obj o : STRUCT.asObjSeq(a2)) {
				assertFalse(o.exists("foo"));
			}
			fail();
		} catch(ClassCastException e) {}
	}
	
	@Test
	public void testSort() {
		Arr a = STRUCT.buildArray(
			STRUCT.buildObject("pos", 3),
			STRUCT.buildObject("pos", 1),
			STRUCT.buildObject("pos", 0),
			STRUCT.buildObject("pos", 2)
		);
		STRUCT.sort(a, new Comparator<Obj>() {
			@Override
			public int compare(Obj o1, Obj o2) {
				return (int)(o1.getLong("pos") - o2.getLong("pos"));
			}
		});
		assertTrue(a.getObject(0).getLong("pos") == 0);
		
		Arr b = STRUCT.buildArray(3, 2, 5, 6, 0, 1);
		STRUCT.sort(b, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});
		assertTrue(b.getLong(0) == 0);
		
		Arr c = STRUCT.buildArray("abcd", "abc", "a", "ab");
		STRUCT.sort(c, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.length() - o2.length();
			}
		});
		assertEquals(c.getString(0), "a");
	}
}
