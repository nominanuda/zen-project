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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nominanuda.urispec.StringMapURISpec;

import static org.junit.Assert.*;

public class BindTest {

	@Test
	public void bindTest() {
		StringMapURISpec t = new StringMapURISpec("{a}");
		Map<String, Object> m = t.match("AAA");
		assertEquals("AAA", m.get("a"));
		
		t = new StringMapURISpec("/{a */0*}/{b}/c");
		m = t.match("/A/AA/B/c");
		assertEquals("A/AA", m.get("a"));

		t = new StringMapURISpec("/{a */0*}/{b */0*}/c");
		m = t.match("/A/AA/B/BB/c");
		assertEquals("A/AA/B", m.get("a"));

		t = new StringMapURISpec("/{a}/{c}/({c */*}/)");
		m = t.match("/a/v/e/r/y/longpath");
		assertNull(m);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	public void bindParamsTest() {
		StringMapURISpec t = new StringMapURISpec("/?{a}");
		Map<String, Object> m = t.match("/?a=1&a=a&a=efoo");
		assertEquals("efoo", ((List<String>)m.get("a")).get(2));
		assertNotNull(t.match("/?a=1&a=a&a=efoo&b=9"));
		assertNull(new StringMapURISpec("/?{a}&").match("/?a=1&a=a&a=efoo&b=9"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimple() {
		StringMapURISpec t = new StringMapURISpec("/?{a}");
		Map<String, Object> m = t.match("/?a=1&a=a&a=efoo");
		assertEquals("efoo", ((List<String>)m.get("a")).get(2));
		assertNotNull(t.match("/?a=1&a=a&a=efoo&b=9"));
		assertNull(new StringMapURISpec("/?{a}&").match("/?a=1&a=a&a=efoo&b=9"));
	}

}
