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

import java.io.*;
import java.util.*;

import org.junit.*;
import org.xml.sax.*;


public class DataStructHelperTest {

	@Test
	public void testToString() {
		assertEquals("{}", new DataObjectImpl().toString());
		assertEquals("[]", new DataArrayImpl().toString());
		DataObject obj = new DataObjectImpl();
		DataArray arr = new DataArrayImpl();
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
		DataObject obj = new DataObjectImpl();
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
		DataObject obj = (DataObject)new JSONParser().parse(
			"{\"a\":null,\"b\":{\"c\":1},\"d\":\"X\"}");
		DataObject obj2 = new DataStructHelper().clone(obj);
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
		DataObject val1 = new DataObjectImpl();
		DataObject val2 = new DataObjectImpl();
		DataArray arr = new DataArrayImpl();
		arr.add(val1);
		arr.add(val2);
		List list = new DataStructHelper().toMapsAndLists(arr);
		assertEquals(2, list.size());
		assertEquals(list.get(0), list.get(1));
	}
	

}
