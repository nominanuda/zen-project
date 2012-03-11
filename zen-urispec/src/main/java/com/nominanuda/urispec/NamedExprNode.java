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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import static com.nominanuda.urispec.URISpecLexer.*;
import static com.nominanuda.urispec.Assert.isFalse;

public class NamedExprNode extends AnonExprNode implements SeqComponent /*BindingTree*/ {
	private String exprName;

	public NamedExprNode(Token payload) {
		super(payload);
	}

	public List<String> templateMulti(Object model) {
		return templateMultiInternal(model, KEEP_SLASHES);
	}

	private List<String> templateMultiInternal(Object model, int encodingOpts) {
		List<String> vals = getNodeAdapter()
			.getStringModelAdapter().getAsList(model,exprName);
		if (vals != null) {
			List<String> encodedVals = new LinkedList<String>();
			for(String s : vals) {
				encodedVals.add(percentEncodeUtf8(s, encodingOpts));
			}
			return encodedVals;
		} else {
			for(CharSequenceNode seq : alts) {
				List<String> tpls = seq.templateMulti(model);
				if(tpls != null) {
					return tpls;
				}
			}
			return null;
		}
	}

	public void initNode() {
		isFalse(valid);
		Assert.isTrue(getChildCount() > 0);
		if (getChildCount() == 1) {
			addChild(getNodeAdapter()
				.newSeqTreeWithCharChildren(new CommonToken(
					MATCHER, "**")));
		}
		alts = new ArrayList<CharSequenceNode>(getChildCount());
		@SuppressWarnings("unchecked")
		List<Tree> l = (List<Tree>) getChildren();
		exprName = l.get(0).getText();
		if(exprName == null) {
			throw new NullPointerException();
		}
		int len = l.size();
		for (int i = 1; i < len; i++) {
			CharSequenceNode seq = (CharSequenceNode) l.get(i);
			seq.initNode();
			alts.add(seq);
		}
		RegexpMatcher _brex = calcBindingRegexp();
		List<String> grList = _brex.getGroups();
		Collections.reverse(grList);
		grList.add(exprName);
		Collections.reverse(grList);
		String r = "("+_brex.getRegexp().substring(3);
		regexpMatcher = new RegexpMatcher(grList, r, getNodeAdapter().getStringModelAdapter());
	}

	public String getVarName() {
		return exprName;
	}
}
