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

import static com.nominanuda.urispec.URISpecLexer.*;
import static com.nominanuda.urispec.Assert.fail;
import static com.nominanuda.urispec.Assert.fail2;

import java.util.regex.Pattern;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;


public class CharactersNode extends AbstractNode implements SeqComponent {
	private String template;
	private boolean hasAnyMatcher = false;
	private Pattern matchingRegexp;

	public CharactersNode(Token payload) {
		super(payload);
	}
	@Override
	public String template(Object model) {
		return hasAnyMatcher ? null : template;
	}

	@Override
	public void initNode() {
		StringBuilder sb = new StringBuilder();
		for(Object child : children) {
			sb.append(getRex(((CommonTree)child).getToken()));
		}
		template = sb.toString();
		matchingRegexp = Pattern.compile(template);
	}

	private String getRex(Token t) {
		String txt = t.getText();
		switch (t.getType()) {
		case URI_CHARS:
			return txt.replace("\\", "");//TODO 
		case MATCHER:
			hasAnyMatcher = true;
			String wc = txt.substring(1, txt.length() -1);//asterisk removal
			return
			"" .equals(wc) ? "[^/]+" :
			"/" .equals(wc) ? ".+" :
			"0" .equals(wc) ? "[^/]*" :
			"/0".equals(wc) ? ".*" :
			wc.startsWith("~") && wc.endsWith("~") ? 
				Pattern.compile(wc
					.substring(2, wc.length()-2)
					.replace("\\~", "~"))
					.toString()
			: fail2();
		default:
			throw fail();
		}
	}

	public RegexpMatcher getBindingRegexp() {
		return new RegexpMatcher(matchingRegexp.toString(), 
				getNodeAdapter().getStringModelAdapter());
	}
}
