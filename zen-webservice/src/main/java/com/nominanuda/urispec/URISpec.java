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

import java.io.StringReader;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;


public class URISpec<T> {
	private final String canonicalSpec;
	private final URISpecNode uriSpecTree;

	@SuppressWarnings("unchecked")
	public URISpec(String spec, StringModelAdapter<?> ma) {
		try {
			canonicalSpec = spec.replace("+", "%20");
			CharStream src = CharStreams.fromReader(new StringReader(canonicalSpec));
			UriSpecLexer lexer = new UriSpecLexer(src);
			CommonTokenStream dd = new CommonTokenStream(lexer);
			UriSpecParser parser = new UriSpecParser(dd);
			parser.setErrorHandler(new BailErrorStrategy());
			ParserRuleContext tree = new ParserRuleContext();
			parser.setContext(tree);
			ParseTree /*ProgramContext*/ pt = parser.program();
			NodeAdapter treeAdaptor = new NodeAdapter();
			treeAdaptor.setModelAdapter((StringModelAdapter<? super Object>)ma);
			AstCompatGeneratingVisitor<Void> visitor = new AstCompatGeneratingVisitor<Void>(treeAdaptor);
			visitor.visit(pt);
			CommonTree root = visitor.getRoot();
			uriSpecTree = (URISpecNode)root.getChild(0);
			uriSpecTree.initNode();
		} catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	public String template(T model) {
		return uriSpecTree.template(model);
	}

	@SuppressWarnings("unchecked")
	public T match(String uri) {
		Object model = uriSpecTree.getNodeAdapter().getStringModelAdapter().createStringModel();
		return (T)(uriSpecTree.match(uri, model) > -1 ? model : null);
	}

	@Override
	public String toString() {
		return canonicalSpec;
	}
}
