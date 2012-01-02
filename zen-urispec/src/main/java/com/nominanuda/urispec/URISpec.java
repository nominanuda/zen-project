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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;


public class URISpec<T> {
	private final String canonicalSpec;
	private final URISpecNode uriSpecTree;

	@SuppressWarnings("unchecked")
	public URISpec(String spec, StringModelAdapter<?> ma) {
		try {
			canonicalSpec = spec;
			ANTLRStringStream src = new ANTLRStringStream(canonicalSpec);
			URISpecLexer lexer = new URISpecLexer(src);
			CommonTokenStream dd = new CommonTokenStream(lexer);
			URISpecParser p = new URISpecParser(dd);
			NodeAdapter treeAdaptor = 
				new NodeAdapter();
			treeAdaptor.setModelAdapter((StringModelAdapter<? super Object>)ma);
			p.setTreeAdaptor(treeAdaptor);
			uriSpecTree = (URISpecNode)p.program().tree;
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
