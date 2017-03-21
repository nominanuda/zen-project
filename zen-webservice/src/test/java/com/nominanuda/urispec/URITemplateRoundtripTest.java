/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, VebA0ron 2.0 (the "License");
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.nominanuda.urispec.StringMapURISpec;



public class URITemplateRoundtripTest {
	@Test
	public void testRegressions() {
		roundTrip("/path1/ping", "/path1/ping");
		roundTrip("/caza/bA0r/content/{path */*}", "/caza/bA0r/content/pa/th");
		roundTrip("/111/{xxx}/{yyy}", "/111/aaa/bbb");
		roundTrip("/{rrr}/path4/{path2}", "/rrr/path4/path2");
		roundTrip("/{rrr}/path3/{cmd}", "/rrr/path3/cmd");
		roundTrip("/path1/rrr/", "/path1/rrr/");
		roundTrip("/path1/ggggg", "/path1/ggggg");
		roundTrip("/path1/", "/path1/");
		roundTrip("/path1/static/{resource */*}", "/path1/static/re/11/rce");
		roundTrip("/{rrr}/dadas/{cmd}/{name}", "/rrr/dadas/cmd/name");
		//TODO BUG roundTrip("/path1/rrr/{rrr}(|/config)", "/path1/rrr/rrr", "/path1/rrr/rrr");
		roundTrip("/path1/rrr/{rrr}/path2/({path2}(/path9))", "/path1/rrr/rrr/path2/");
		roundTrip("/path1/rrr/{rrr}/path2/({path2}(/path9))", "/path1/rrr/rrr/path2/db/path9");
		roundTrip("/path1/rrr/{rrr}/path2/({path2}(/path9))", "/path1/rrr/rrr/path2/db", "/path1/rrr/rrr/path2/db/path9");
		roundTrip("/path1/rrr/{rrr}(/config)", "/path1/rrr/rrr", "/path1/rrr/rrr/config");
		roundTrip("/path1/rrr/{rrr}(/config)", "/path1/rrr/rrr/config", "/path1/rrr/rrr/config");
		//TODO roundTrip("/{rrr}/{path2}/({dir */*}/)", "/rr+r/path2/d/i/r/");
		roundTrip("/{rrr}/{path2}/({dir */*}/)", "/rrr/path2/d/i/r/");
		roundTrip("/{rrr}/{path2}(/{dir */*}/)", "/rrr/path2");
		roundTrip("/query/bA0r/qtag", "/query/bA0r/qtag");
		roundTrip("/query/bA0r/fulltext", "/query/bA0r/fulltext");
		roundTrip("/{rrr}/{path2}/{resource */*}", "/rrr/path2/resource");
		roundTrip("/{rrr}/{path2}/{resource */*}", "/rrr/path2/resou/rce");
		roundTrip("/favicon.ico", "/favicon.ico");
		roundTrip("/ping", "/ping");
		roundTrip("/query/{path */*}", "/query/p/ath");
		roundTrip("/caza/bA0r/zz/{path */*}", "/caza/bA0r/zz/path");
		roundTrip("/(track|album|artist).getSuggested", "/track.getSuggested");
		roundTrip("/{entity track|album|artist}.getSuggested", "/track.getSuggested");
		roundTrip("/{entity track|album|artist}.getSuggested", "/album.getSuggested");
	}
	@Test
	public void testRoundTrips() {
		roundTrip("/foo", "/foo");
		roundTrip("/foo", "/./foo/.././foo", "/foo");
		roundTrip("/foo?", "/foo");
		roundTrip("/fo%2Fo?", "/fo%2Fo");
		roundTrip("/fo%2fo?", "/fo%2fo");
		roundTrip("/foo?s=", "/foo?s=");
		roundTrip("/foo?s={x$foo}", "/foo?s=foo");
		roundTrip("/foo?s={x$foo}&(f=1)", "/foo?s=foo&f=1");
		roundTrip("/%20foo?s={x$foo}&(f=1)", "/%20foo?s=foo&f=1");
	}
	
	private void roundTrip(String uriTpl, String uri) {
		roundTrip(uriTpl, uri, uri);
	}
	private void roundTrip(String uriTpl, String uri, String expectedTpl) {
		StringMapURISpec template = new StringMapURISpec(uriTpl);
		Map<String, Object> m = (Map<String, Object>)template.match(uri);
		assertNotNull(m);
		String tpl = template.template(m);
		assertEquals(expectedTpl, tpl);
	}
}
