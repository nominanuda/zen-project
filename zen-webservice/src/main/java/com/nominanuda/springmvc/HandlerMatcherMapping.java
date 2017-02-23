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

import com.nominanuda.web.mvc.HandlerAndFilters;
import com.nominanuda.web.mvc.HandlerFilter;
import com.nominanuda.web.mvc.HandlerMatcher;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class HandlerMatcherMapping implements HandlerMapping, ApplicationContextAware {
	private HandlerMatcher handlerMatcher;
	private transient DispatcherServletHelper dispatcherServletHelper;
	private ApplicationContext applicationContext;


	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		HttpRequest httpRequest = SERVLET.getOrCreateRequest(request, true);
		Tuple2<Object, Stru> res = handlerMatcher.match(httpRequest);
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
			SERVLET.storeCommand(request, res.get1());
			return hec;
		}
	}

	private HandlerInterceptor convert(final HandlerFilter f) {

		return new HandlerInterceptor() {
			public boolean preHandle(HttpServletRequest request,
					HttpServletResponse response, Object handler)
					throws Exception {
				HttpRequest req = SERVLET.getOrCreateRequest(request, true);
				Stru cmd = Check.ifNull(SERVLET.getCommand(request), Obj.make());
				Object handlerOutput = f.before(req, cmd, handler);
				if (handlerOutput == null) {
					return true;
				} else {
					SERVLET.storeHandlerOutput(request, handlerOutput);
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
				f.after(SERVLET.getOrCreateRequest(request, true),
						SERVLET.getCommand(request), handler,
						SERVLET.getHandlerOutput(request));
			}

			public void afterCompletion(HttpServletRequest request,
					HttpServletResponse response, Object handler, Exception ex)
					throws Exception {
				f.afterCompletion(
						SERVLET.getOrCreateRequest(request, true),
						SERVLET.getResponse(request), handler,
						SERVLET.getHandlerOutput(request), ex);
			}
		};
	}
	
	
	
	/* setters */

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void setHandlerMatcher(HandlerMatcher handlerMatcher) {
		this.handlerMatcher = handlerMatcher;
	}
}
