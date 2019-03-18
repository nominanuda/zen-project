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

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.obj.wrap.ObjWrapper;

public abstract class GenericHandlerAdapter implements HandlerAdapter, HttpProtocol {
	protected abstract Object handleInternal(Object handler, HttpRequest request, Stru command) throws Exception;

	public Object invoke(Object handler, HttpRequest request, Stru command) throws Exception {
		Object handlerResponse = handleInternal(handler, request, command);
		return adaptHandlerResponse(handlerResponse);
	}

	protected final Object adaptHandlerResponse(Object response) throws Exception {
		Check.notNull(response);
		if (response instanceof ObjWrapper) {
			return new JsonEntity(((ObjWrapper) response).unwrap());
		}
		if (response instanceof Stru) {
			Stru stru = (Stru)response;
			if (stru.isObj()) {
				Obj obj = stru.asObj();
				String view = obj.getStr("view_");
				if (view != null) {
					return (view.startsWith("redirect:"))
						? HTTP.redirectTo(view.substring("redirect:".length()))
						: new PathAndJsonViewSpec(view, obj.getObj("data_"));
				}
			}
			return new JsonEntity(stru);
		}
		if (response instanceof ViewSpec
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
