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

import static org.junit.Assert.fail;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import com.nominanuda.urispec.NodeAdapter;



public class URITemplateParseTest {
	private static final String[] validExprs = new String[] {
		"a",
		"////",
		"////?#",
		"http(s)://({user}:{pass}@)google.com:90",
		"/{user}",
		"/({user})",
		"(/{user a})",
		"{user}",
		"{user a}",
		"{user **}",
		"/?",
		"/?a=0",
		"/schedule/query/latestNewsWithVideo?(startIndex=0)&count=4&network=la1",
//		"/artist.getTracks?{artistCode}AND{step}AND{page}&country=it",
	};
	private static final String[] invalidExprs = new String[] {
		"??",
		"?",
		"#",
		"{}",
		"/a{}",
		"?a=0",
		"/?a",
		"/\"  \"?",
	};

	@Test
	public void testExprs() throws Exception {
		for (String s : validExprs) {
			System.err.println("about to parse "+s);
			parse(s);
		}
		for (String s : invalidExprs) {
			System.err.println("about to parse wrong expr "+s);
			try {
				parse(s);
				fail();
			} catch (Exception e) {}
		}
	}

	private void parse(String uriPattern) throws RecognitionException {
		ANTLRStringStream src = new ANTLRStringStream(uriPattern);
		URISpecLexer lexer = new URISpecLexer(src);
		CommonTokenStream dd = new CommonTokenStream(lexer);
		URISpecParser p = new URISpecParser(dd);
		p.setTreeAdaptor(new NodeAdapter());
		p.program();
	}
}
