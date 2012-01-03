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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.http.client.HttpClient;

import static com.nominanuda.lang.Check.*;

public class HttpClientHyperApiFactory implements HyperApiFactory {
	private HttpClient client;
	private String uriPrefix;

	public <T> T getInstance(String instanceHint, Class<? extends T> cl) {
		InvocationHandler handler = new HyperApiHttpInvocationHandler(cl, client, 
			ifNull(uriPrefix,"")+ifNull(instanceHint,""));
		Object p = Proxy.newProxyInstance(cl.getClassLoader(),
				new Class[] { cl }, handler);
		return cl.cast(p);
	}

	public void setHttpClient(HttpClient client) {
		this.client = client;
	}

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

}
