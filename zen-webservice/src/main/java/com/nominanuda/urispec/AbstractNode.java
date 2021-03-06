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
import static java.util.Arrays.asList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * {@link AbstractNode#template(Object)} and {@link AbstractNode#templateMulti(Object)} are
 * both implemented but at least one of then must be overridden
 */
public abstract class AbstractNode extends CommonTree implements TemplatingNode {
	private NodeAdapter nodeAdapter;
	protected boolean valid = false;

	public AbstractNode(Token token) {
		super(token);
	}
	protected NodeAdapter getNodeAdapter() {
		return nodeAdapter;
	}

	public List<String> templateMulti(Object model) {
		String s = template(model);
		return s == null ? null : asList(s);
	}
	public String template(Object model) {
		List<String> res = templateMulti(model);
		return res == null ? null : res.get(0);
	}

	public void initNode() {
		isFalse(valid);
		if(children != null) {
			for(Object child : children) {
				if(child instanceof AbstractNode) {
					((AbstractNode)child).initNode();
				}
			}
		} else {
			children = new LinkedList<Object>();
		}
		valid = true;
	}
	public void setNodeAdaptor(NodeAdapter adaptor) {
		this.nodeAdapter = adaptor;
	}

	protected static final int KEEP_SLASHES = 1;

	public String percentEncodeUtf8(String str, int opts) {
		try {
			String encoded = URLEncoder.encode(str, "UTF-8");
			if(opts == KEEP_SLASHES) {
				encoded = encoded.replace("%2F", "/");
			}
			return encoded;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
