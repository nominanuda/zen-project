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
package com.nominanuda.web.mvc;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.urispec.URISpec;

@ThreadSafe
public class URISpecMatcher implements HandlerMatcher {
	private static final String ALLHTTPMETHODS = 
		"GET|POST|PUT|DELETE|HEAD|OPTIONS|TRACE";
	private static final Pattern PIPEDMETHODS = Pattern.compile(
		"\\s*\\p{Upper}+(?:\\s*\\|\\s*\\p{Upper}+)*\\s*");
	private Object handler;
	private URISpec<DataObject> spec;
	private Pattern methodPattern;
	private List<HandlerFilter> handlerFilters = null;
	private transient HandlerAndFilters handlerAndFilters = null;

	public @Nullable Tuple2<Object, DataStruct> match(HttpRequest request) {
		String method = request.getRequestLine().getMethod();
		Matcher methodMatcher = methodPattern.matcher(method);
		if(! methodMatcher.matches()) {
			return null;
		}

		DataObject o = spec.match(request.getRequestLine().getUri());
		return o == null ? null :
			new Tuple2<Object, DataStruct>(getHandlerOrChain(), o);
	}

	private Object getHandlerOrChain() {
		if(handlerFilters == null) {
			return handler;
		} else {
			if(handlerAndFilters == null) {
				handlerAndFilters = new HandlerAndFiltersBean(handler, handlerFilters);
			}
			return handlerAndFilters;
		}
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}
	public void setSpec(String spec) {
		spec = spec.trim();
		String method = null;
		String urispec = null;
		if(spec.contains(" ")) {
			String[] parts = spec.split("\\s+");
			if(PIPEDMETHODS.matcher(parts[0]).matches()) {
				method = parts[0];
				urispec = spec.substring(parts[0].length()).trim();
			} else {
				method = ALLHTTPMETHODS;
				urispec = spec;
			}
		} else {
			method = ALLHTTPMETHODS;
			urispec = spec;
		}
		methodPattern = Pattern.compile(method);
		this.spec = new URISpec<DataObject>(urispec,
				new DataObjectStringModelAdapter());
	}

	public void setHandlerFilters(List<HandlerFilter> hfs) {
		handlerFilters = new LinkedList<HandlerFilter>();
		handlerFilters.addAll(hfs);
	}
}
