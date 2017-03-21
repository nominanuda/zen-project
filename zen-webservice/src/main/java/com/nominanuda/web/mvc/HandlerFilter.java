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

import javax.annotation.Nullable;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.zen.obj.Stru;

public interface HandlerFilter {
	/**
	 * 
	 * @param request
	 * @param cmd
	 * @param handler
	 * @return null or an object to be treated as the handler return value; in the latter case
	 * execution of the chain should be suspended
	 * @throws Exception
	 */
	@Nullable Object before(HttpRequest request, Stru cmd, Object handler)
			throws Exception;

	void after(HttpRequest request, Stru cmd, Object handler,
			Object handlerReturnValue) throws Exception;

	void afterCompletion(HttpRequest request, HttpResponse response,
			Object handler, Object handlerReturnValue, Exception ex)
			throws Exception;

}
