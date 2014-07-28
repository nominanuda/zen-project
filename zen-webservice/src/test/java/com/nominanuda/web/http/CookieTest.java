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
package com.nominanuda.web.http;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Test;

import com.nominanuda.web.http.HttpCoreHelper.CookieSpecKind;

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

public class CookieTest extends BaseHttpTest {
	private boolean serverFailed = false;

	@Test
	public void test() throws Exception {
		startServer(9876, new AbstractHandler() {
			public void handle(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				try {
					HttpRequest req = new ServletHelper().copyRequest(request, false);
					HttpResponse resp;
					List<Cookie> lc = HTTP.getRequestCookiesByName(req, "FOO");
					if(lc.isEmpty()) {
						resp = HTTP.createBasicResponse(200,"x",HttpProtocol.CT_TEXT_PLAIN_CS_UTF8);
						HTTP.setResponseCookie(resp, "FOO", "BAR", CookieSpecKind.browserCompat);
					} else {
						resp = HTTP.createBasicResponse(200,"xxx",HttpProtocol.CT_TEXT_PLAIN_CS_UTF8);
					}
					new ServletHelper().copyResponse(resp, response);
				} catch(Exception e) {
					serverFailed = true;
				}
			}
		});
		
		HttpClient cli = HTTP.createClient(1000000, 1000000, 1000000);
		HttpGet req = new HttpGet("http://localhost:9876/");
		HttpResponse resp = cli.execute(req);
		assertEquals(1, resp.getEntity().getContentLength());
		resp = cli.execute(req);
		assertEquals(3, resp.getEntity().getContentLength());
		assertFalse(serverFailed);
	}
}
