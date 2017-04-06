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
package com.nominanuda.hyperapi;

import static com.nominanuda.zen.common.Check.ifNull;

import java.lang.reflect.Proxy;

import org.apache.http.client.HttpClient;

import static com.nominanuda.zen.common.Str.STR;

public class HttpClientHyperApiFactory extends ExceptionCatcherFactory implements HyperApiFactory {
	private boolean allowExceptions = true;
	private HttpClient httpClient;
	private String uriPrefix;
	private String userAgent;

	public <T> T getInstance(String instanceHint, Class<? extends T> apiInterface) {
		String prefix = ifNull(uriPrefix, "") + ifNull(instanceHint, "");
		return apiInterface.cast(Proxy.newProxyInstance(apiInterface.getClassLoader(), new Class[] { apiInterface },
			new HyperApiHttpInvocationHandler(httpClient, prefix, userAgent, allowExceptions ? exceptionRenderer : null)));
	}
	
	/**
	 * This one allows to spring-configure both remote prefix and local implementation of apiInterface:
	 * if remote url is blank then local implementation will be used.
	 * @param instanceHint
	 * @param apiImpl
	 * @param apiInterface
	 * @return
	 */
	public <T> T getInstance(String instanceHint, Class<? extends T> apiInterface, T apiImpl) {
		return STR.notNullOrBlank(instanceHint) ? getInstance(instanceHint, apiInterface) : allowExceptions ? apiImpl : getInstance(apiImpl, apiInterface);
	}
	public <T> T getInstanceNoHint(Class<? extends T> apiInterface, T apiImpl) { // useful when commenting out instanceHint line in Spring xml
		return getInstance(null, apiInterface, apiImpl);
	}
	
	
	/* setters */
	
	public void setAllowExceptions(boolean allow) {
		allowExceptions = allow;
	}
	
	public void setHttpClient(HttpClient client) {
		this.httpClient = client;
	}

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
