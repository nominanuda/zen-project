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

import static com.nominanuda.web.http.ServletHelper.SERVLET;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.nominanuda.web.mvc.HandlerAdapter;
import com.nominanuda.web.mvc.PathAndJsonViewSpec;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class HandlerAdapterWrapper implements org.springframework.web.servlet.HandlerAdapter {
	private HandlerAdapter handlerAdapter;

	@Override
	public boolean supports(Object handler) {
		return handlerAdapter.supports(handler);
	}

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpRequest httpReq = SERVLET.getOrCreateRequest(request, true);
		Stru command = Check.ifNull(SERVLET.getCommand(request), Obj.make());
		final Object result = handlerAdapter.invoke(handler, httpReq, command);
		SERVLET.storeHandlerOutput(request, result);
		if (result instanceof HttpResponse) {
			return new ModelAndView(new View() {
				@Override public String getContentType() {
					return null;
				}
				@Override public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
					SERVLET.copyResponse((HttpResponse)result, response);
				}
			});
		} else if (result instanceof HttpEntity) {
			return new ModelAndView(new HttpEntityView((HttpEntity)result));
		} else if (result instanceof PathAndJsonViewSpec) {
			PathAndJsonViewSpec viewSpec = (PathAndJsonViewSpec)result;
			return new ModelAndView(viewSpec.getPath(), viewSpec.getModel().asObj());
		} else if (result instanceof ModelAndView) {
			return (ModelAndView)result;
		} else {
			throw new IllegalStateException();
		}
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return 0;
	}

	public void setHandlerAdapter(HandlerAdapter handlerAdapter) {
		this.handlerAdapter = handlerAdapter;
	}

}
