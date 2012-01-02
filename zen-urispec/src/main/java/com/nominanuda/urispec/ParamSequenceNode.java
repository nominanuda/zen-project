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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.Token;



public class ParamSequenceNode extends AbstractNode implements ParamMatcher {

	public ParamSequenceNode(Token payload) {
		super(payload);
	}

	@Override
	public String template(Object model) {
		if(children.isEmpty()) {
			return "";
		}
		StringBuilder res = new StringBuilder();
		Iterator<?> itr = children.iterator();
		boolean shouldAmpersAppend = false;
		while(itr.hasNext()) {
			String s = ((TemplatingNode)itr.next()).template(model);
			if(s == null) {
				return null;
			} else {
				if(shouldAmpersAppend && ! "".equals(s)) {
					res.append("&");
				}
				shouldAmpersAppend = true;
				res.append(s);
			}
		}
		return res.toString();
	}

	public List<String> matchParam(Map<String, List<String>> params, Object model) {
		StringModelAdapter<? super Object> ma = getNodeAdapter().getStringModelAdapter();
		Object m = ma.createStringModel();
		List<String> res = new LinkedList<String>();
		for(Object child : children) {
			List<String> boundParams = ((ParamMatcher)child).matchParam(params, m);
			if(boundParams == null) {
				return null;
			}
			res.addAll(boundParams);
		}
		ma.setAll(m, model);
		return res;
	}
}
