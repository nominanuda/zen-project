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
package com.nominanuda.solr;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.servlet.SolrDispatchFilter;
import org.springframework.web.HttpRequestHandler;

import com.nominanuda.lang.Check;


public class SpringMvcServerEmbed implements FilterChain, HttpRequestHandler {
	private SolrDispatchFilter solrDispatchFilter;
	private String notHandledReqAttr = "NO_HANDLER_FOUND";

	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		request.setAttribute(notHandledReqAttr, true);
	}

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		solrDispatchFilter.doFilter(request, response, this);
	}

	public void init() throws ServletException {
		FilterConfig fc = new FilterConfig() {
			public String getInitParameter(String name) {
				return null;
			}
			public ServletContext getServletContext() {
				throw new UnsupportedOperationException();
			}
			public String getFilterName() {
				throw new UnsupportedOperationException();
			}
			@Override
			public Enumeration<String> getInitParameterNames() {
				throw new UnsupportedOperationException();
			}
		};
		(solrDispatchFilter = new SolrDispatchFilter()).init(fc);
		Check.illegalstate.assertFalse(SolrAware.getInstance().getCoreNames().isEmpty());
	}

	public void destroy() {
		solrDispatchFilter.destroy();
	}

	public void setNotHandledReqAttr(String notHandledReqAttr) {
		this.notHandledReqAttr = notHandledReqAttr;
	}
}
