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

import java.io.IOException;
import java.io.StringReader;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;



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

	private void parse(String uriPattern) throws IOException {
		CharStream src = CharStreams.fromReader(new StringReader(uriPattern));
		UriSpecLexer lexer = new UriSpecLexer(src);
		CommonTokenStream dd = new CommonTokenStream(lexer);
		UriSpecParser parser = new UriSpecParser(dd);
		ParserRuleContext tree = new ParserRuleContext();
		parser.setContext(tree);
		parser.setErrorHandler(new BailErrorStrategy());
		//p.setTreeAdaptor(new NodeAdapter());
		ParseTree /*ProgramContext*/ pt = parser.program();
//		ParserRuleContext tree = parser.getRuleContext();
		UriSpecBaseListener listener = new UriSpecBaseListener();
//		ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
//		walker.walk(listener, tree);
		ParseTreeWalker.DEFAULT.walk(listener, pt);
	}
}
