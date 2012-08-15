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

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.http.HttpProtocol;

public abstract class GenericHandlerAdapter implements HandlerAdapter, HttpProtocol {
	protected static final HttpCoreHelper httpCoreHelper = new HttpCoreHelper();

	protected abstract Object handleInternal(Object handler, HttpRequest request,
			DataStruct command) throws Exception;

	public Object invoke(Object handler, HttpRequest request,
			DataStruct command) throws Exception {
		Object handlerResponse = handleInternal(handler, request, command);
		return adaptHandlerResponse(handlerResponse);
	}

	protected final Object adaptHandlerResponse(Object response) throws Exception {
		Check.notNull(response);
		if(response instanceof ViewSpec) {
			return response;
		} else if(response instanceof DataStruct) {
			DataStruct ds = (DataStruct)response;
			if(ds instanceof DataObject && ((DataObject)ds).exists("view_")) {
				String view = ((DataObject)ds).getString("view_");
				if(view.startsWith("redirect:")) {
					String redirUrl = view.substring("redirect:".length());
					HttpResponse resp = httpCoreHelper.createBasicResponse(302);
					resp.setHeader(HDR_LOCATION, redirUrl);
					return resp;
				} else {
					DataObject data = ((DataObject)ds).getObject("data_");
					PathAndJsonViewSpec mav = new PathAndJsonViewSpec(view, data);
					return mav;
				}
			} else {
				return new JsonEntity(ds);
			}
		} else if(response instanceof HttpEntity) {
			return response;
		} else if(response instanceof HttpResponse) {
			return response;
		} else {
			throw new HttpException("cannot render response of type "
					+ response.getClass().getName());
		}
	}

	protected HttpResponse createResponse(int status) {
		return httpCoreHelper.createBasicResponse(status);
	}

	public static Object unwrapHandlerIfNeeded(Object handlerOrHandlerAndFilters) {
		if(handlerOrHandlerAndFilters instanceof HandlerAndFilters) {
			return ((HandlerAndFilters)handlerOrHandlerAndFilters).getHandler();
		} else {
			return handlerOrHandlerAndFilters;
		}
	}
}
