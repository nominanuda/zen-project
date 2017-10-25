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

import static com.nominanuda.zen.common.Check.illegalargument;
import static com.nominanuda.zen.io.Uris.URIS;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.QueryResponseWriterUtil;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.servlet.ResponseUtils;
import org.apache.solr.servlet.cache.HttpCacheHeaderUtil;
import org.apache.solr.servlet.cache.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

/*
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
<property name="mappings">
	<props>
		<prop key="/core1/**">solrCore1CoreHandler</prop>
	</props>
</property>
</bean>

<bean id="solrCore1CoreHandler" class="com.nominanuda.solr.SolrHttpRequestHandler" lazy-init="true">
	<property name="core" ref="core1"/>
	<property name="prefix" value="/core1"/>
</bean>

<bean class="org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter"/>
*/

public class SolrHttpRequestHandler implements HttpRequestHandler {
	private final Logger log = LoggerFactory.getLogger(SolrHttpRequestHandler.class);
	private SolrCore core;
	private String prefix;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LocalSolrQueryRequest solrReq = null;
		core.open();
		try {
			Map<String, String> command = extractQueryParams(request);
			SolrParams q = new MapSolrParams(command);
			String ru = request.getRequestURI();
			String requestPrefix = URIS.pathJoin(request.getContextPath(),prefix);
			illegalargument.assertTrue(ru.startsWith(requestPrefix), "request " + ru + " must start with " + requestPrefix);
			URI reqUri = URI.create(ru.substring(requestPrefix.length()));
			String reqPath = reqUri.getPath();
			solrReq = new LocalSolrQueryRequest(core, q);

			final Method reqMethod = Method.getMethod(request.getMethod());
			HttpCacheHeaderUtil.setCacheControlHeader(core.getSolrConfig(), response, reqMethod);
			// unless we have been explicitly told not to, do cache validation
			// if we fail cache validation, execute the query
			if (core.getSolrConfig().getHttpCachingConfig().isNever304()
					|| !HttpCacheHeaderUtil.doCacheHeaderValidation(solrReq, request, reqMethod, response)) {
				SolrQueryResponse solrRsp = new SolrQueryResponse();
				/*
				 * even for HEAD requests, we need to execute the handler to ensure
				 * we don't get an error (and to make sure the correct
				 * QueryResponseWriter is selected and we get the correct
				 * Content-Type)
				 */
				SolrRequestInfo.setRequestInfo(new SolrRequestInfo(solrReq, solrRsp));
				SolrRequestHandler handler = core.getRequestHandler(reqPath);
				solrReq.getContext().put("webapp", request.getContextPath());
				core.execute(handler, solrReq, solrRsp);

				HttpCacheHeaderUtil.checkHttpCachingVeto(solrRsp, response, reqMethod);
				Iterator<Map.Entry<String, String>> headers = solrRsp.httpHeaders();
				while (headers.hasNext()) {
					Map.Entry<String, String> entry = headers.next();
					response.addHeader(entry.getKey(), entry.getValue());
				}
				QueryResponseWriter responseWriter = core.getQueryResponseWriter(solrReq);
				// writeResponse(solrRsp, responseWriter, reqMethod);

				Object invalidStates = solrReq.getContext().get(CloudSolrClient.STATE_VERSION);
				// This is the last item added to the response and the client would
				// expect it that way.
				// If that assumption is changed , it would fail. This is done to
				// avoid an O(n) scan on
				// the response for each request
				if (invalidStates != null) {
					solrRsp.add(CloudSolrClient.STATE_VERSION, invalidStates);
				}

				final String ct = responseWriter.getContentType(solrReq, solrRsp);

				if (null != ct) {
					response.setContentType(ct);
				} else {
					response.setContentType("application/octect-stream");//?? maybe not the correct behaviour
				}

				if (solrRsp.getException() != null) {
					NamedList<?> info = new SimpleOrderedMap<>();
					int code = ResponseUtils.getErrorInfo(solrRsp.getException(), info, log);
					solrRsp.add("error", info);
					response.setStatus(code);
				}

				if (Method.HEAD != reqMethod) {
					OutputStream out = new CloseShieldOutputStream(response.getOutputStream());// Prevent close of container streams see SOLR-8933
					QueryResponseWriterUtil.writeQueryResponse(out, responseWriter, solrReq, solrRsp, ct);
				}// else http HEAD request, nothing to write out, waited this long just
			} else {
				//uglily handled in HttpCacheHeaderUtil.doCacheHeaderValidation
			}
		} finally {
			consumeInputFully(request);
			try {
				try {
					if (solrReq != null) {
						log.debug("Closing out SolrRequest: {}", solrReq);
						solrReq.close();
					}
				} finally {
					if (core != null) {
						core.close();
					}
				}
			} finally {
				SolrRequestInfo.clearRequestInfo();
			}
		}
	}

	// we make sure we read the full client request so that the client does
	// not hit a connection reset and we can reuse the
	// connection - see SOLR-8453 and SOLR-8683
	private void consumeInputFully(HttpServletRequest req) {
		try {
			ServletInputStream is = req.getInputStream();
			while (!is.isFinished() && is.read() != -1) {
			}
		} catch (IOException e) {
			log.info("Could not consume full client request", e);
		}
	}

	private Map<String, String> extractQueryParams(HttpServletRequest request) {
		Map<String, String> map = new LinkedHashMap<>();
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue()[0]);
		}
		return map;
	}

	public void setCore(SolrCore core) {
		this.core = core;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
