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

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

import static com.nominanuda.urispec.URISpecLexer.*;

public class NodeAdapter extends CommonTreeAdaptor implements TreeAdaptor {
	private StringModelAdapter<? super Object> adapter;

	@SuppressWarnings("unchecked")
	public void setModelAdapter(StringModelAdapter<?> modelAdapter) {
		adapter = (StringModelAdapter<? super Object>)modelAdapter;
	}

	public StringModelAdapter<? super Object> getStringModelAdapter() {
		return adapter;
	}
	@Override
	public Object create(Token token) {
		Object tree = createNode(token);
		if(tree instanceof AbstractNode) {
			((AbstractNode)tree).setNodeAdaptor(this);
		}
		return tree;
	}

	public Object createNode(Token token) {
		if(token != null) {
			switch (token.getType()) {
			case URI_SPEC:
				return new URISpecNode(token);
			case SCHEME_AUTH_PATH_PART:
				return new PathNode(token);
			case QUERY_PART:
				return new QueryNode(token);
			case FRAGMENT_PART:
				return new FragmentNode(token);
			case ANONYMOUS_EXPRESSION:
				return new AnonExprNode(token);
			case CHARACTERS:
				return new CharactersNode(token);
			case NAMED_EXPRESSION:
				return new NamedExprNode(token);
			case PARAM_DECL:
				return new ParamDeclarationNode(token);
			case PARAM_GROUP:
				return new ParamGroupNode(token);
			case SEQUENCE:
				return new CharSequenceNode(token);
			case PARAM_SEQUENCE:
				return new ParamSequenceNode(token);
			case STRICT_PARAMS:
				return super.create(token);
			}
		}
		return super.create(token);
	}
	public ParamSequenceNode createEmptyParamSequence() {
		ParamSequenceNode pseq = new ParamSequenceNode(new CommonToken(PARAM_SEQUENCE));
		pseq.setNodeAdaptor(this);
		return pseq;
	}
	public CharSequenceNode newSeqTreeWithCharChildren(Token... tokens) {
		CharactersNode anyCharsTree = createCharacters(tokens);
		CharSequenceNode seqTree = new CharSequenceNode(new CommonToken(SEQUENCE));
		seqTree.addChild(anyCharsTree);
		seqTree.setNodeAdaptor(this);
		return seqTree;
	}
	public CharSequenceNode createEmptyCharSequence() {
		CharactersNode anyCharsTree = createCharacters(
				new CommonToken(URI_CHARS, ""));
		CharSequenceNode seq = new CharSequenceNode(new CommonToken(SEQUENCE));
		seq.addChild(anyCharsTree);
		seq.setNodeAdaptor(this);
		return seq;
	}
	public CharactersNode createCharacters(Token... tokens) {
		CharactersNode anyCharsTree = new CharactersNode(new CommonToken(CHARACTERS));
		for(Token t : tokens) {
			anyCharsTree.addChild(new CommonTree(t));
		}
		anyCharsTree.setNodeAdaptor(this);
		return anyCharsTree;
	}
}
