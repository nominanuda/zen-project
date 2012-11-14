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
package com.nominanuda.web.htmlcomposer;

import static org.junit.Assert.assertTrue;

import java.util.Stack;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.web.http.HttpProtocol;

public class CssMatchTest {

	@Test
	public void test() {
		Stack<HtmlTag> ancestors = new Stack<HtmlTag>();
		ancestors.push(new HtmlTag("body", atts()));
		ancestors.push(new HtmlTag("form", atts("class","aform")));
		ancestors.push(new HtmlTag("div", atts("id","thediv")));
		match("div p", "p", atts(), ancestors);
		match("div > p", "p", atts(), ancestors);
		match("body p", "p", atts(), ancestors);
		match("form.aform > div#thediv >  p", "p", atts(), ancestors);
		match("#uglyman", "input", atts("id", "uglyman"), ancestors);
		match("div > *", "p", atts(), ancestors);
		match("*[miao=\"bau\"]", "p", atts("miao", "bau"), ancestors);
	}

	private void match(String cssExpr, String tag, Attributes atts, Stack<HtmlTag> ancestors) {
		JquerySelectorExpr expr = new JquerySelectorExpr(cssExpr);
		assertTrue(expr.matches(tag, atts, ancestors));
	}

	private Attributes atts(String... ss) {
		AttributesImpl a = new AttributesImpl();
		for(int i = 0; i < ss.length/2; i++) {
			a.addAttribute(HttpProtocol.HTMLNS, ss[i*2], ss[i*2], "", ss[i*2+1]);
		}
		return a;
	}
}
