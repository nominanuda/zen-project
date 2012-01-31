package com.nominanuda.hibernate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.jsonparser.JSONParser;
import com.nominanuda.dataobject.jsonparser.ParseException;
import com.nominanuda.hibernate.ObjectExpander;
import com.nominanuda.hibernate.PathMap;

public class ObjectExpanderTest {

	@Test
	public void testExpand() throws Exception {
		Map<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("t1", "b");
		ObjectExpander oe = new ObjectExpander();
		oe.setExpandedMaps(typeMap);
		String src = "{" +
                "\"mmType\":\"t1\"," +
                "\"a\":1," +
                "\"b\":{" +
                        "\"oid\":\"OID\"," +
                        "\"a\":1}," +
                "\"c\":{" +
                        "\"a\":2," +
                        "\"d\":{" +
                                "\"a\":3}}," +
                "\"e\":{" +
                        "\"a\":22," +
                        "\"d\":{" +
                                "\"a\":4}}}";
		Map<String, ? extends Object> m = parse(src);
		DataObject res = oe.expand(m, "t1", false);
		System.err.println(res.toString());
		// TODO
		// Assert.assertEquals("{\"mmType\": \"t1\", \"a\": 1, \"b\": {\"oid\": \"OID\"}}",
		// oe.expand(m, false).toString());
		// System.err.println(oe.expand(m, true).toString());
		// Assert.assertEquals("{\"mmType\": \"t1\", \"a\": 1, \"b\": {\"oid\": \"OID\", \"a\": 1}}",
		// oe.expand(m, true).toString());
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

	protected Map<String, ? extends Object> parse(String src) throws IOException,
			ParseException, SAXException {
		JSONParser p = new JSONParser();
		DataObject o = (DataObject)p.parse(src);
		DataStructHelper h = new DataStructHelper();
		return h.toMapsAndSetLists(o);
	}
}