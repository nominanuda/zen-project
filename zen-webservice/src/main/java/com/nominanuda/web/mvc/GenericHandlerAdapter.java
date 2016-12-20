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

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.web.servlet.ModelAndView;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.HttpProtocol;

public abstract class GenericHandlerAdapter implements HandlerAdapter, HttpProtocol {
	protected abstract Object handleInternal(Object handler, HttpRequest request, DataStruct command) throws Exception;

	public Object invoke(Object handler, HttpRequest request, DataStruct command) throws Exception {
		Object handlerResponse = handleInternal(handler, request, command);
		return adaptHandlerResponse(handlerResponse);
	}

	protected final Object adaptHandlerResponse(Object response) throws Exception {
		Check.notNull(response);
		if (response instanceof DataStruct) {
			DataStruct ds = (DataStruct)response;
			if (ds.isObject()) {
				DataObject obj = ds.asObject();
				String view = obj.getString("view_");
				if (view != null) {
					return (view.startsWith("redirect:"))
						? HTTP.redirectTo(view.substring("redirect:".length()))
						: new PathAndJsonViewSpec(view, obj.getObject("data_"));
				}
			}
			return new JsonEntity(ds, "text/plain"); // text/plain for old shitty m$ browsers
		} else if (response instanceof ViewSpec
				|| response instanceof HttpEntity
				|| response instanceof HttpResponse
				|| response instanceof ModelAndView) {
			return response;
		}
		throw new HttpException("cannot render response of type " + response.getClass().getName());
	}

	public static Object unwrapHandlerIfNeeded(Object handlerOrHandlerAndFilters) {
		if (handlerOrHandlerAndFilters instanceof HandlerAndFilters) {
			return ((HandlerAndFilters)handlerOrHandlerAndFilters).getHandler();
		} else {
			return handlerOrHandlerAndFilters;
		}
	}
}
