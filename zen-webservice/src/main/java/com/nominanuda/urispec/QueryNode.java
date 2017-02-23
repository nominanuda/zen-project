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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import static com.nominanuda.urispec.UriSpecParser.*;


public class QueryNode extends AbstractNode implements ParamMatcher {

	public QueryNode(Token token) {
		super(token);
	}
	@Override
	public String template(Object model) {
		return ((TemplatingNode)getChild(0)).template(model);
	}

	public List<String> matchParam(Map<String, List<String>> params, Object model) {
		ParamSequenceNode pseq = (ParamSequenceNode)getChild(0);
		if(isStrictParams()) {
			StringModelAdapter<? super Object> ma = getNodeAdapter().getStringModelAdapter();
			Object m = ma.createStringModel();
			List<String> l = pseq.matchParam(params, m);
			if(l == null) {
				return null;
			}
			Set<String> pNames = params.keySet();
			pNames.removeAll(l);
			if(pNames.isEmpty()) {
				ma.setAll(m, model);
				return l;
			} else {
				return null;
			}
		} else {
			return pseq.matchParam(params, model);
		}
	}
	
	private boolean isStrictParams() {
		return getFirstChildWithType(STRICT_PARAMS) != null;
	}

}
