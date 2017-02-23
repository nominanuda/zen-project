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

import static com.nominanuda.urispec.Assert.isFalse;
import static com.nominanuda.urispec.Assert.isTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;

public class AnonExprNode extends AbstractNode implements TemplatingNode, SeqComponent/*BindingTree*/ {
	protected List<CharSequenceNode> alts;
	protected RegexpMatcher regexpMatcher;

	public AnonExprNode(Token payload) {
		super(payload);
	}

	public RegexpMatcher getBindingRegexp() {
		return regexpMatcher;
	}

	public List<String> templateMulti(Object model) {
		for(CharSequenceNode seq : alts) {
			List<String> tpls = seq.templateMulti(model);
			if(tpls != null) {
				return tpls;
			}
		}
		return null;
	}

	public void initNode() {
		isFalse(valid);
		isTrue(getChildCount() > 0);
		if(getChildCount() == 1) {//add empty alternative
			addChild(getNodeAdapter().createEmptyCharSequence());
		}
		alts = new ArrayList<CharSequenceNode>(getChildCount());
		for(Object child : getChildren()) {
			CharSequenceNode seq = (CharSequenceNode)child;
			seq.initNode();
			alts.add(seq);
		}
		regexpMatcher = calcBindingRegexp();
	}

	protected RegexpMatcher calcBindingRegexp() {
		List<String> vars = new LinkedList<String>();
		StringBuilder rex = new StringBuilder();
		rex.append("(?:");
		Iterator<CharSequenceNode> itr = alts.iterator();
		while(itr.hasNext()) {
			CharSequenceNode seq = itr.next();
			RegexpMatcher brex = seq.getBindingRegexp();
			vars.addAll(brex.getGroups());
			rex.append("(?:"+brex.getRegexp()+")");
			if(itr.hasNext()) {
				rex.append("|");
			}
		}
		rex.append(")");
		return new RegexpMatcher(vars, rex.toString(),
				getNodeAdapter().getStringModelAdapter());
	}
}
