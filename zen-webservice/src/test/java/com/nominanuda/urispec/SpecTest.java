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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;


public class SpecTest {
	private void test(String tpl, String res, Object... objs) {
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		for (int i = 0; i < objs.length; i += 2) {
			map.put((String)objs[i], objs[i+1]);
		}
		StringMapURISpec t = new StringMapURISpec(tpl);
		String result = t.template(map);
		if(res != null || result != null) {
			assertEquals(res, result);
		}
	}
	@Test
	public void testPath() {
		test("../a.,","../a.,");
		test("///","///");
		test("/{a {b {c}}}","/C","c","C");
	}
	@Test
	public void testFullURI() {
		test("http://a:b@hello.world:12?times=&from=me","http://a:b@hello.world:12?times=&from=me");
		test("classpath:com.foo.MyClass", "classpath:com.foo.MyClass");
		test("classpath:/META-INF/readme.txt", "classpath:/META-INF/readme.txt");
		test("file:///tmp/{tmpFile}", "file:///tmp/1","tmpFile",1);
		test("http://10.10.10.10", "http://10.10.10.10");
		test("file:///tmp/{tmpFile */0*}", "file:///tmp/1/2","tmpFile","1/2");
		test("urn:isbn:{isbn}", "urn:isbn:123","isbn",123);
		test("{scheme}:{subscheme}:{id}", "urn:uuid:123-245","scheme","urn","subscheme","uuid","id","123-245");
		test("{scheme}:({id})", "urn:","scheme","urn");
		test("({scheme}{id}|{id})", "ID","id","ID");
		test("/({scheme}{id}|({id}))", "/");
		test("/({scheme}{id}|(http:{id}))", "/");
		test("({scheme}{id}|(http:{id}))", "http:1", "id", 1);
		test("({scheme}{id}|http:({id}))", "http:1", "id", 1);
		test("({scheme}{id}|http:({id}))", "http:");
	}
	@Test
	public void testQuery() {
		test("/?{a}", "/?a=A","a","A");
		test("/?{a}&b=&c={cc}&({d})", "/?a=A&b=&c=CC", "a","A","cc","CC");
		test("/?{a}&b=&c={cc}&({d})", "/?a=A&b=&c=CC&d=D", "a","A","cc","CC","d","D");
		test("/?({a}&{b}|{c})", "/?a=A&b=", "a","A","b","");
		test("/?({a}&{b}|{c})", "/?a=A&b=", "a","A","b","","c","");
		test("/?({a}&{b}|{c})", "/?c=", "a","A","c","");
		test("/?{a}&({b})", "/?a=", "a","");
		test("/?{a}&({b})", "/?a=1", "a","1");
		test("/?{a}&({b})", "/?a=1&b=2", "a","1", "b","2");
		test("/?{a} b=2", "/?a=1&b=2", "a","1");
	}
	@Test
	public void testQueryMval() {
		test("/?{a}", "/?a=A&a=AA&a=","a", asList("A","AA",""));
	}
	
	@Test
	public void testRegressions() {
		test("/?{a}", "/?a=A&a=AA&a=","a", asList("A","AA",""));
	}

	@Test
	public void testRegressionJsWeb() {
		//TODO
//		StringMapURISpec spec = new StringMapURISpec("/somepath/{tpl **.soy.js}?{lang en|it}");

	}
	@Test
	public void testCatalog() {
		StringMapURISpec spec = new StringMapURISpec("http://54.93.119.44:8080/musicloud-ingex/api/catalog?{apikey} {codes} ({locale}) ({channel}) ({tstamp})");
		
	}

	//http://54.93.119.44:8080/musicloud-ingex/api/catalog?{apikey} {codes} ({locale}) ({channel}) ({tstamp})
}
