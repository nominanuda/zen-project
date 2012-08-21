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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.dataobject.DataStruct;

public class NoOpHandlerFilter implements HandlerFilter {

	public Object before(HttpRequest request, DataStruct cmd, Object handler)
			throws Exception {
		return null;
	}

	public void after(HttpRequest request, DataStruct cmd, Object handler,
			Object handlerReturnValue) throws Exception {
	}

	public void afterCompletion(HttpRequest request, HttpResponse response,
			Object handler, Object handlerReturnValue, Exception ex)
			throws Exception {
	}

}