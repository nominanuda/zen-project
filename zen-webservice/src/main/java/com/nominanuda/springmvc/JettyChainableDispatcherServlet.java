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

import static com.nominanuda.zen.classwork.Reflect.REFL;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

public class JettyChainableDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = -3687969916503048020L;
	private static final String UNHANDLED_REQUEST = "UNHANDLED_REQUEST";
	private static final String SETHANDLED_FALSE_CALLED = "SETHANDLED_FALSE_CALLED";

	@Override
	protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (request.getAttribute(SETHANDLED_FALSE_CALLED) == null) {
			if (REFL.safeInstanceOf(request, "org.eclipse.jetty.server.Request")
			|| REFL.safeInstanceOf(request, "org.mortbay.jetty.Request")) {
				REFL.invokeMethod(request, "setHandled", new Object[] { false });
			}
			request.setAttribute(SETHANDLED_FALSE_CALLED, true);
		}
	}
	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpContext.getInstance().init(request);
		super.doDispatch(request, response);
		if (request.getAttribute(UNHANDLED_REQUEST) != null) {
			noHandlerFound(request, response);
		}
	}
	
	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpContext.getInstance().writeTo(response);
		super.render(mv, request, response);
	}

	public static void markRequestAsUnhandled(ServletRequest request) {
		request.setAttribute(UNHANDLED_REQUEST, true);
	}

}