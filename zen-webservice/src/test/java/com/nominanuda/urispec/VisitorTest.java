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

import java.io.IOException;
import java.io.StringReader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class VisitorTest {

	@Test
	public void test() throws IOException {
		parse("http://www.google.com/bau/?w={qq}#miao");
		//fail("Not yet implemented");
	}
	private void parse(String uriPattern) throws IOException {
		ANTLRInputStream src = new ANTLRInputStream(new StringReader(uriPattern));
		UriSpecLexer lexer = new UriSpecLexer(src);
		CommonTokenStream dd = new CommonTokenStream(lexer);
		UriSpecParser parser = new UriSpecParser(dd);
		ParserRuleContext tree = new ParserRuleContext();
		parser.setContext(tree);
		//p.setTreeAdaptor(new NodeAdapter());
		ParseTree /*ProgramContext*/ pt = parser.program();
		AstCompatGeneratingVisitor<Void> visitor = new AstCompatGeneratingVisitor<>(new NodeAdapter());
		
		//MyVisitor visitor = new MyVisitor(); // extends JavaBaseVisitor<Void>
        // and overrides the methods
        // you're interested
		//visitor.visit(tree);
		visitor.visit(pt);
		CommonTree root = visitor.getRoot();
		printAst(root, 0);
//		ParseTreeWalker.DEFAULT.walk(visitor, pt);
		
//		ParserRuleContext tree = parser.getRuleContext();
		//UriSpecBaseListener listener = new UriSpecBaseListener();
//		ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
//		walker.walk(listener, tree);
//		ParseTreeWalker.DEFAULT.walk(listener, pt);
	}
	private void printAst(Tree t, int deepness) {
		String n = t.getClass().getSimpleName();
		String txt = t.getText();
		int type = t.getType();
		System.err.println(nTimes(deepness, "  ")+deepness+": ["+n+"-"+n+"-"+txt+"-"+type+"]");
		int nc = t.getChildCount();
		for(int i = 0; i < nc; i++) {
			Tree child = t.getChild(i);
			printAst(child, deepness+1);
		}
		
	}

	private String nTimes(int n, String s) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
}
