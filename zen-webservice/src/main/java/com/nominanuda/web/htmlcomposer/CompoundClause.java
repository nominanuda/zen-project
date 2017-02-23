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

import static com.nominanuda.zen.common.Check.illegalstate;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

public class CompoundClause extends Clause {
	private static final int NOVAL = -1;
	private static final int TARGET = 0;
	private static final int PARENT = 1;
	private static final int ANCESTOR = 2;
	private int clauseScope = NOVAL;
	private List<Clause> clauses = new LinkedList<Clause>();

	public void add(Clause c) {
		clauses.add(c);
	}

	public boolean isUnizializedScope() {
		return clauseScope == NOVAL;
	}
	public void setAncestorScope() {
		clauseScope = ANCESTOR;
	}

	public void setParentScope() {
		clauseScope = PARENT;
	}

	public int match(String tag, Attributes atts, List<HtmlTag> parents,
			int parentRecursionLevel) {
		switch (clauseScope) {
		case TARGET:
			return match(tag, atts) ? parentRecursionLevel : -1;
		case PARENT:
			HtmlTag ht = parents.get(parentRecursionLevel);
			return match(ht.getTagName(), ht.getAttributes())
					? parentRecursionLevel + 1 : -1;
		case ANCESTOR:
			for(int i = parentRecursionLevel; i < parents.size(); i++) {
				HtmlTag htx = parents.get(i);
				if(match(htx.getTagName(), htx.getAttributes())) {
					return i + 1;
				}
			}
			return -1;
		default:
			illegalstate.fail();
			break;
		}
		return 0;
	}

	@Override
	public boolean match(String tag, Attributes atts) {
		for(Clause c : clauses) {
			if(! c.match(tag, atts)) {
				return false;
			}
		}
		return true;
	}

	public void setTargetScope() {
		clauseScope = TARGET;
	}

	public boolean isTargetScope() {
		return clauseScope == TARGET;
	}

}