package com.nominanuda.dataobject;

import static com.nominanuda.zen.obj.wrap.Wrap.WF;
import static org.junit.Assert.*;

import org.junit.Test;

public class DomainDataObjectTest {

	@Test
	public void testExNovo() {
		BizObj o = WF.wrap(BizObj.class);
		o.putString("prop1", "foo");
		assertEquals("foo", o.prop1());
		assertEquals("BizObj", accept(o));
		assertEquals("Obj", accept(o.unwrap()));
	}

	private String accept(Obj o) {
		return "Obj";
	}

	private String accept(BizObj o) {
		return "BizObj";
	}

}
