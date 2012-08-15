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
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.DispatcherServletHelper;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.web.http.ServletHelper;
import com.nominanuda.web.mvc.HandlerAndFilters;
import com.nominanuda.web.mvc.HandlerFilter;
import com.nominanuda.web.mvc.HandlerMatcher;

public class HandlerMatcherMapping implements HandlerMapping, ApplicationContextAware {
	private static final DataStructHelper DS = new DataStructHelper();
	private static final ServletHelper servletHelper = new ServletHelper();
	private HandlerMatcher handlerMatcher;
	private transient DispatcherServletHelper dispatcherServletHelper;
	private ApplicationContext applicationContext;

	public void setHandlerMatcher(HandlerMatcher handlerMatcher) {
		this.handlerMatcher = handlerMatcher;
	}

	public HandlerExecutionChain getHandler(HttpServletRequest request)
			throws Exception {
		HttpRequest httpRequest = servletHelper.getOrCreateRequest(request,
				true);
		;
		Tuple2<Object, DataStruct> res = handlerMatcher.match(httpRequest);
		if (res == null) {
			return null;
		} else {
			Object h = res.get0();
			HandlerExecutionChain hec;
			if (h instanceof HandlerAndFilters) {
				HandlerAndFilters haf = (HandlerAndFilters) h;
				hec = new HandlerExecutionChain(haf.getHandler());
				for (HandlerFilter f : haf.getFilters()) {
					hec.addInterceptor(convert(f));
				}
			} else {
				hec = new HandlerExecutionChain(h);
			}
			servletHelper.storeCommand(request, res.get1());
			return hec;
		}
	}

	private HandlerInterceptor convert(final HandlerFilter f) {

		return new HandlerInterceptor() {
			public boolean preHandle(HttpServletRequest request,
					HttpServletResponse response, Object handler)
					throws Exception {
				HttpRequest req = servletHelper.getOrCreateRequest(request,
						true);
				DataStruct cmd = Check.ifNull(
						servletHelper.getCommand(request), DS.newObject());
				Object handlerOutput = f.before(req, cmd, handler);
				if (handlerOutput == null) {
					return true;
				} else {
					servletHelper.storeHandlerOutput(request, handlerOutput);
					getDispatcherServletHelper().renderHandlerOutput(request, response, handlerOutput);
					return false;
				}
			}

			private DispatcherServletHelper getDispatcherServletHelper() {
				if(dispatcherServletHelper == null) {
					dispatcherServletHelper = new DispatcherServletHelper();
					dispatcherServletHelper.setApplicationContext(applicationContext);
					dispatcherServletHelper.init();
				}
				return dispatcherServletHelper;
			}

			public void postHandle(HttpServletRequest request,
					HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
				f.after(servletHelper.getOrCreateRequest(request, true),
						servletHelper.getCommand(request), handler,
						servletHelper.getHandlerOutput(request));
			}

			public void afterCompletion(HttpServletRequest request,
					HttpServletResponse response, Object handler, Exception ex)
					throws Exception {
				f.afterCompletion(
						servletHelper.getOrCreateRequest(request, true),
						servletHelper.getResponse(request), handler,
						servletHelper.getHandlerOutput(request), ex);
			}
		};
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
