package com.nominanuda.urispec;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PercentEncodeTest {

	@Test
	public void testTemplate() {
		StringMapURISpec spec = new StringMapURISpec("/{baz}?{foo}");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("foo", "ba r");
		model.put("baz", "ba Z");
		assertEquals("/ba+Z?foo=ba+r", spec.template(model));
	}

	@Test
	public void testBind() {
		StringMapURISpec spec = new StringMapURISpec("/{baz}?{foo}");
		Map<String, Object> model = spec.match("/ba+Z?foo=ba+r");
		assertEquals("ba r", model.get("foo"));
		assertEquals("ba Z", model.get("baz"));
	}

	@Test
	public void testBindPerc20() {
		StringMapURISpec spec = new StringMapURISpec("/{baz}?{foo}");
		Map<String, Object> model = spec.match("/ba+Z?foo=ba%20r");
		assertEquals("ba r", model.get("foo"));
		assertEquals("ba Z", model.get("baz"));
	}

	@Test
	public void testBindIri() {
		StringMapURISpec spec = new StringMapURISpec("/{baz}?{foo} {boo}");
		Map<String, Object> model = spec.match("/é?foo=è&boo=%C3%A9");
		assertEquals("è", model.get("foo"));
		assertEquals("é", model.get("baz"));
		assertEquals("é", model.get("boo"));
	}

	@Test
	public void testUtf8Template() {
		StringMapURISpec spec = new StringMapURISpec("/{foo}");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("foo", "é");
		assertEquals("/%C3%A9", spec.template(model));
	}

}
