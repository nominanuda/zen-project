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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

public class ParamDeclarationNode extends AbstractNode implements ParamMatcher {
	private String paramName;
	private AbstractNode valExpr;

	public ParamDeclarationNode(Token payload) {
		super(payload);
	}

	public List<String> matchParam(Map<String, List<String>> params, Object model) {
		List<String> l = params.get(paramName);
		if(l == null) {
			return null;
		} else {
			StringModelAdapter<? super Object> ma = getNodeAdapter().getStringModelAdapter();
			Object m = ma.createStringModel();
			RegexpMatcher brex = ((SeqComponent)valExpr).getBindingRegexp();
			for(String s : l) {
				Object m1 = this.getNodeAdapter().getStringModelAdapter().createStringModel();
				int bound = brex.match(s, m);
				if(bound != s.length()) {
					return null;
				}
				//TODO inefficient, data are copied even if null still may be returned
				ma.pushAll(m1, m); 
			}
			ma.setAll(m, model);
			return Arrays.asList(paramName);
		}
	}

	@Override
	public String template(Object model) {
		List<String> l = valExpr.templateMulti(model);
		if(l == null) {
			return null;
		} else {
			Iterator<String> itr = l.iterator();
			StringBuilder sb = new StringBuilder();
			while(itr.hasNext()) {
				sb.append(paramName + "=" + itr.next());
				if(itr.hasNext()) {
					sb.append("&");
				}
			}
			return sb.toString();
		}
	}

	@Override
	public void initNode() {
		isFalse(valid);
		isFalse(children.isEmpty());
		int nChildren = children.size();
		Tree firstChild = getChild(0);
		if(nChildren == 1 && firstChild instanceof NamedExprNode) {
			NamedExprNode net = (NamedExprNode)firstChild;
			net.initNode();
			paramName = net.getVarName();
			valExpr = net;
		} else if(nChildren == 1) {
			setPName(firstChild);
			valExpr = getNodeAdapter().createEmptyCharSequence();
			addChild(valExpr);
			valExpr.initNode();
		} else {
			isTrue(children.size() == 2);
			setPName(firstChild);
			valExpr = (CharSequenceNode)children.get(1);
			valExpr.initNode();
		}
	}

	private void setPName(Tree firstChild) {
		CommonTree act = (CommonTree)firstChild;
		paramName = act.getText();
	}
}
