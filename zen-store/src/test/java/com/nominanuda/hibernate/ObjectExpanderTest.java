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
package com.nominanuda.hibernate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.ParseException;
import com.nominanuda.hibernate.ObjectExpander;
import com.nominanuda.hibernate.PathMap;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

public class ObjectExpanderTest {

	@Test
	public void testExpand() throws Exception {
		Map<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("t1", "b");
		ObjectExpander oe = new ObjectExpander();
		oe.setExpandedMaps(typeMap);
		String src = "{mmType:'t1',a:1,b:{oid:'OID',a:1},c:{a:2,d:{a:3}},e:{a:22,d:{a:4}}}";
		Map<String, Object> m = parse(src);
		DataObject res = oe.expand(m, "t1", false);
		System.err.println(res.toString());
//TODO ????
//		Assert.assertEquals(STRUCT.parse("{mmType: 't1',a:1,b:{oid:'OID'}}", true).toString(),
//		oe.expand(m, "mmType,t1,a,b", false).toString());
//		System.err.println(oe.expand(m, "", true).toString());
//		Assert.assertEquals("{\"mmType\":\"t1\",\"a\": 1,\"b\":{\"oid\":\"OID\",\"a\": 1}}",
//		oe.expand(m, "",true).toString());
	}

	@Test
	public void testPathMap() {
		PathMap dummy = new PathMap("");
		Assert.assertFalse(dummy.isTraversable("foo"));
		Assert.assertEquals("", dummy.soFar());
		try {
			dummy.traverse("foo");
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}
		PathMap p = new PathMap("a.b,c.(d,e.(f,g))");
		Assert.assertTrue(p.traverse("a").isTraversable("b"));
		Assert.assertFalse(p.traverse("a").isTraversable("c"));
		Assert.assertTrue(p.traverse("c").traverse("e").isTraversable("g"));
		Assert.assertFalse(p.traverse("c").traverse("e").isTraversable("b"));
		Assert.assertEquals("c.e.g", p.traverse("c").traverse("e")
				.traverse("g").soFar());
	}

	protected Map<String,Object> parse(String src) throws IOException,
			ParseException, SAXException {
		DataObject o = STRUCT.parseObject(src, true);
		DataStructHelper h = new DataStructHelper();
		return h.toMapsAndSetLists(o);
	}
}