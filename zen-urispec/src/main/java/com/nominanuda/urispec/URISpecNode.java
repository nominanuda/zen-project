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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.Token;


public class URISpecNode extends AbstractNode implements MatchingNode {
	private PathNode path;
	private QueryNode query;
	private FragmentNode fragment;
	
	public URISpecNode(Token payload) {
		super(payload);
	}
	@Override
	public String template(Object model) {
		StringBuilder sb = new StringBuilder();
		String p = path.template(model);
		if(p == null) {
			return null;
		}
		sb.append(p);
		if(query != null) {
			String q = query.template(model);
			if(q == null) {
				return null;
			}
			if(q.length() > 0) {
				sb.append("?").append(q);
			}
		}
		if(fragment != null) {
			String f = fragment.template(model);
			if(f == null) {
				return null;
			}
			if(f.length() > 0) {
				sb.append("#").append(f);
			}
		}
		return sb.toString();
	}
	@Override
	public void initNode() {
		isFalse(valid);
		super.initNode();
		path = (PathNode) getChild(0);
		query = (QueryNode) getChild(1);
		fragment = (FragmentNode) getChild(2);
	}

	public int match(String pattern, Object model) {
		URI uri = URI.create(pattern).normalize();
		String q = uri.getRawQuery();
		int qlen = q == null ? 0 : q.length() + /* the '?' */1;
		String f = uri.getRawFragment();
		int flen = f == null ? 0 : f.length() + /* the '#' */1;
		String uriStr = uri.toString();
		int len = uriStr.length();
		String p = uriStr.substring(0, len-qlen-flen);
		StringModelAdapter<? super Object> ma = getNodeAdapter().getStringModelAdapter();
		Object m = ma.createStringModel();

		if(path.match(p, m) < p.length()) {
			return -1;
		}
		if(query != null) {
			if(q == null) {
				q = "";
			}
			Map<String, List<String>> pMap = Utils.parseQueryString(q);
			List<String> l = query.matchParam(pMap, m);
			if(l == null) {
				return -1;
			}
		}
		if(fragment != null) {
			if(path.match(p, m) < 0) {
				return -1;
			}
		}
		ma.setAll(m, model);
		return pattern.length();
	}
}
