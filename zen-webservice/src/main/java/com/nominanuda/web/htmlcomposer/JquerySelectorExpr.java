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
package com.nominanuda.web.htmlcomposer;

import static com.nominanuda.zen.common.Str.STR;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;

class JquerySelectorExpr {
	private List<Expr> l = new LinkedList<Expr>();
	public JquerySelectorExpr(String expr) {
		List<String> commaSep = STR.splitAndTrim(expr, ",");
		for(String cse : commaSep) {
			l.add(new Expr(cse));
		}
	}
	
	public boolean matches(String tag, Attributes atts, Stack<HtmlTag> parents) {
		List<HtmlTag> _parents = new LinkedList<HtmlTag>(parents);
		Collections.reverse(_parents);
		for(Expr exp : l) {
			if(exp.matches(tag, atts, _parents)) {
				return true;
			}
		}
		return false;
	}
	private class Expr {
		private List<CompoundClause> clauses = new LinkedList<CompoundClause>();
		public Expr(String cse) {
			cse = cse.replace("[", " [");
			List<String> bits = STR.splitAndTrim(cse, "\\s+");
			Collections.reverse(bits);
			CompoundClause pendingClause = new CompoundClause();
			for(int i = 0; i < bits.size(); i++) {
				String bit = bits.get(i);
				if(Clause.isAttributeClause(bit)) {
					pendingClause.add(Clause.build(bit));
				} else {
					Clause c = Clause.build(bit);
					pendingClause.add(c);
					if(i < bits.size() - 2 && ">".equals(bits.get(i + 1))) {
						if(clauses.size() > 0 && pendingClause.isUnizializedScope() ) {
							pendingClause.setAncestorScope();
						} else if(pendingClause.isUnizializedScope() ){
							pendingClause.setTargetScope();
						}
						clauses.add(pendingClause);
						pendingClause = new CompoundClause();
						pendingClause.setParentScope();
						i++;
					} else {
						if(clauses.size() > 0 && pendingClause.isUnizializedScope() ) {
							pendingClause.setAncestorScope();
						} else if(pendingClause.isUnizializedScope() ){
							pendingClause.setTargetScope();
						}
						clauses.add(pendingClause);
						pendingClause = new CompoundClause();
					}
				}
			}
		}

		public boolean matches(String tag, Attributes atts,
				List<HtmlTag> parents) {
			int parentRecursionLevel = 0;
			int parentChainLen = parents.size();
			for(CompoundClause c : clauses) {
				if(parentRecursionLevel > parentChainLen - 1 && ! c.isTargetScope()) {
					return false;
				}
				parentRecursionLevel = c.match(tag, atts, parents, parentRecursionLevel);
				if(parentRecursionLevel == -1) {
					return false;
				}
			}
			return true;
		}
		
	}
}