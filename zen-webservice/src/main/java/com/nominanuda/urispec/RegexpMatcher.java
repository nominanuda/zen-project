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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpMatcher {
	private List<String> groups = new LinkedList<String>();
	private String rex;
	private StringModelAdapter<? super Object> adapter;

	public RegexpMatcher(List<String> groups, String regexp, StringModelAdapter<? super Object> modelAdapter) {
		this.groups.addAll(groups);
		this.rex = regexp;
		this.adapter = modelAdapter;
	}

	public RegexpMatcher(String regexp, StringModelAdapter<? super Object> modelAdapter) {
		this.rex = regexp;
		this.adapter = modelAdapter;
	}

	public List<String> getGroups() {
		return groups;
	}

	public String getRegexp() {
		return rex;
	}
	public RegexpMatcher append(RegexpMatcher other) {
		rex = rex + other.getRegexp();
		groups.addAll(other.getGroups());
		return this;
	}

	private Matcher matcher(String expr) {
		return Pattern.compile(rex).matcher(expr);
	}

	public int match(String pattern, Object model) {
		Matcher m = matcher(pattern);
		if(m.find()) {
			int ngroups = m.groupCount();
			for(int i = 1; i <= ngroups; i++) {
				String captured = m.group(i);
				if(captured != null) {
					adapter.push(model, groups.get(i - 1), captured);
				}
			}
			return m.group().length();
		} else {
			return -1;
		}
	}
}
