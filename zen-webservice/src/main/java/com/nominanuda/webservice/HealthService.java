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
package com.nominanuda.webservice;

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

import java.util.Set;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.WebService;

public class HealthService implements WebService {
	
	public static interface HealthCheck {
		void check() throws Exception;
	}
	
	
	private final Set<HealthCheck> checks;
	
	public HealthService(Set<HealthCheck> checks) {
		this.checks = checks;
	}
	

	@Override
	public HttpResponse handle(HttpRequest request) throws Exception {
		for (HealthCheck check : checks) {
			check.check();
		}
		return HTTP.createBasicResponse(200, "healthy", HttpProtocol.CT_TEXT_PLAIN_CS_UTF8);
	}

}
