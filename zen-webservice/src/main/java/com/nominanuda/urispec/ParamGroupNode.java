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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;


public class ParamGroupNode extends AbstractNode implements ParamMatcher {
	protected List<ParamSequenceNode> alternatives;

	public ParamGroupNode(Token payload) {
		super(payload);
	}

	public String template(Object model) {
		for(ParamSequenceNode seq : alternatives) {
			String tpl = seq.template(model);
			if(tpl != null) {
				return tpl;
			}
		}
		return null;
	}

	public void initNode() {
		isFalse(valid);
		isTrue(getChildCount() > 0);
		if(getChildCount() == 1) {
			addChild(getNodeAdapter().createEmptyParamSequence());
		}
		alternatives = new ArrayList<ParamSequenceNode>(getChildCount());
		for(Object child : getChildren()) {
			ParamSequenceNode seq = (ParamSequenceNode)child;
			seq.initNode();
			alternatives.add(seq);
		}
	}

	public List<String> matchParam(Map<String, List<String>> params, Object model) {
		StringModelAdapter<? super Object> adapter = getNodeAdapter().getStringModelAdapter();
		Object m = adapter.createStringModel();
		Object firstEmptyModel = null;
		for(ParamSequenceNode pseq : alternatives) {
			List<String> l = pseq.matchParam(params, m);
			if(l != null && l.isEmpty() && firstEmptyModel == null) {
				firstEmptyModel = m;
				m = adapter.createStringModel();
			} else if(l != null) {
				adapter.setAll(m, model);
				return l;
			}
		}
		if(firstEmptyModel != null) {
			adapter.setAll(firstEmptyModel, model);
			return Collections.emptyList();
		} else {
			return null;
		}
	}
}
