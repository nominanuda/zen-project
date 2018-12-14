/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.wro4j;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class WroFilter implements Filter {
	private static final String TOKEN = "/wro4j.";
	private final ro.isdc.wro.http.WroFilter wro = new ro.isdc.wro.http.WroFilter();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		wro.init(filterConfig);
	}
	
	@Override
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (((HttpServletRequest) request).getRequestURI().contains(TOKEN)) {
			wro.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
		wro.destroy();
	}
}
