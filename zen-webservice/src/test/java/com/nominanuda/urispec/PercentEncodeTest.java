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
package com.nominanuda.urispec;

import static org.junit.Assert.*;

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
