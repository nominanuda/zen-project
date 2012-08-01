package com.nominanuda.dataobject;

import static org.junit.Assert.*;

import org.junit.Test;

public class LazyDataObjectTest {

	@Test
	public void test() {
		JsonLooseParser p = new JsonLooseParser();
		String json = p.parseObject("{a:1,b:false,c:[null,'moo']}").toString();
		LazyDataObject o = new LazyDataObject(json);
		assertEquals(json, o.toString());
		assertFalse(o.isExploded());
		assertEquals("moo", o.getArray("c").get(1));
		assertTrue(o.isExploded());
		assertEquals(json, o.toString());
	}

}
