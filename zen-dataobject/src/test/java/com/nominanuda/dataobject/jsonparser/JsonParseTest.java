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
package com.nominanuda.dataobject.jsonparser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.nominanuda.dataobject.Arr;
import com.nominanuda.dataobject.Obj;
import com.nominanuda.dataobject.DataStructContentHandler;
import com.nominanuda.dataobject.JSONParser;
import com.nominanuda.dataobject.ParseException;


public class JsonParseTest {

	@Test
	public void testParseObject() throws IOException, ParseException, SAXException {
		String json = "{\"a\":1,\"b\":[{\"c\":false}]}";
		JSONParser p = new JSONParser();
		DataStructContentHandler ch = new DataStructContentHandler();
		p.parse(json, ch);
		Obj o = (Obj)ch.getResult();
		Assert.assertEquals(1L, o.get("a"));
		Assert.assertEquals(false, ((Obj)((Arr)o.get("b")).get(0)).get("c"));
	}
	
	@Test
	public void testParseArray() throws IOException, ParseException, SAXException {
		String json = "[1,null,2]";
		JSONParser p = new JSONParser();
		DataStructContentHandler ch = new DataStructContentHandler();
		p.parse(json, ch);
		Arr a = (Arr)ch.getResult();
		Assert.assertEquals(2L, a.get(2));
		Assert.assertNull(a.get(1));
	}
}
