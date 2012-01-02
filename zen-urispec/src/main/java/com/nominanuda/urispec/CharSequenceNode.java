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

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;


public class CharSequenceNode extends AbstractNode implements SeqComponent, MatchingNode {
	protected RegexpMatcher regexpMatcher;
	protected boolean singleChild = false;

	public CharSequenceNode(Token payload) {
		super(payload);
	}

	@Override
	public void initNode() {
		isFalse(valid);
		super.initNode();
		singleChild = children.size() == 1;
		regexpMatcher = calcBindingRegexp();
	}

	private RegexpMatcher calcBindingRegexp() {
		RegexpMatcher br = null;
		for(Object child : children) {
			if(br == null) {
				br = ((SeqComponent)child).getBindingRegexp();
			} else {
				br = br.append(((SeqComponent)child).getBindingRegexp());
			}
		}
		return br;
	}

	@Override
	public List<String> templateMulti(Object model) {
		if(singleChild) {
			return ((TemplatingNode)children.get(0)).templateMulti(model);
		} else {
			StringBuilder res = new StringBuilder();
			for(Object child : children) {
				String s = ((TemplatingNode)child).template(model);
				if(s == null) {
					return null;
				} else {
					res.append(s);
				}
			}
			return Arrays.asList(res.toString());
		}
	}

	public int match(String pattern, Object model) {
		Object m = this.getNodeAdapter().getStringModelAdapter().createStringModel();
		int consumed = regexpMatcher.match(pattern, m);
		if(consumed < 0) {
			return -1;
		} else {
			StringModelAdapter<? super Object> ma = getNodeAdapter().getStringModelAdapter();
			ma.setAll(m, model);
			return consumed;
		}
	}

	public RegexpMatcher getBindingRegexp() {
		return regexpMatcher;
	}
}
