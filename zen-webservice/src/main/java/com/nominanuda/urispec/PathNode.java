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

import org.antlr.v4.runtime.Token;


public class PathNode extends AbstractNode implements MatchingNode {

	public PathNode(Token token) {
		super(token);
	}

	@Override
	public String template(Object model) {
		return ((TemplatingNode)getChild(0)).template(model);
	}
	public int match(String pattern, Object model) {
		return ((MatchingNode)getChild(0)).match(pattern, model);
	}

}
