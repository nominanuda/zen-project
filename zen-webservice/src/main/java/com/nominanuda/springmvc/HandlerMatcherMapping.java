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
package com.nominanuda.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.web.http.ServletHelper;
import com.nominanuda.web.mvc.HandlerMatcher;

public class HandlerMatcherMapping implements HandlerMapping {
	private static final ServletHelper servletHelper = new ServletHelper();
	private HandlerMatcher handlerMatcher;
	
	public void setHandlerMatcher(HandlerMatcher handlerMatcher) {
		this.handlerMatcher = handlerMatcher;
	}

	public HandlerExecutionChain getHandler(HttpServletRequest request)
			throws Exception {
		HttpRequest httpRequest = servletHelper.getOrCreateRequest(request, true);;
		Tuple2<Object, DataStruct> res = handlerMatcher.match(httpRequest);
		if(res == null) {
			return null;
		} else {
			HandlerExecutionChain hec = new HandlerExecutionChain(res.get0());
			servletHelper.storeCommand(request, res.get1());
			return hec;
		}
	}
}
