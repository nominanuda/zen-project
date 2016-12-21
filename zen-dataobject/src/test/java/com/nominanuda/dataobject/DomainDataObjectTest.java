package com.nominanuda.dataobject;

import static com.nominanuda.dataobject.WrappingFactory.WF;
import static org.junit.Assert.*;

import org.junit.Test;

public class DomainDataObjectTest {

	@Test
	public void testExNovo() {
		BizObj o = WF.wrap(BizObj.class);
		o.putString("prop1", "foo");
		assertEquals("foo", o.prop1());
		assertEquals("BizObj", accept(o));
		assertEquals("DataObject", accept(o.unwrap()));
	}

	private String accept(DataObject o) {
		return "DataObject";
	}

	private String accept(BizObj o) {
		return "BizObj";
	}

}
