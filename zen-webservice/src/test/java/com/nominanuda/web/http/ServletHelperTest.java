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


import java.io.*;
import static org.junit.Assert.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.message.*;
import org.apache.http.util.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.junit.*;
import org.springframework.mock.web.*;

public class ServletHelperTest extends BaseHttpTest {
	ServletHelper servletHelper = new ServletHelper();

	@Test
	public void testGetRequestLineURI() {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/foo%20bar?z=+1");
		String reqLineURI = servletHelper.getRequestLineURI(req, false);
		System.err.println(reqLineURI);
	}

	@Test
	public void testCopyRequest() throws Exception {
		final String msg = "miào";
		final String mediaType = CT_APPLICATION_OCTET_STREAM;
		Server server = startServer(10000, new AbstractHandler() {
			public void handle(String arg0, Request jettyReq, HttpServletRequest servletReq,
					HttpServletResponse arg3) throws IOException, ServletException {
				HttpRequest r = servletHelper.copyRequest(servletReq, false);
				asyncAssertEquals("bar", r.getFirstHeader("X-foo").getValue());
				asyncAssertEquals("PUT", r.getRequestLine().getMethod());
				HttpEntity e = ((HttpEntityEnclosingRequest)r).getEntity();
				asyncAssert(msg.getBytes("UTF-8").length == e.getContentLength(), "length");
				asyncAssert(e.getContentType().getValue().startsWith(mediaType));
				asyncAssertEquals(mediaType, EntityUtils.getContentMimeType(e));
				asyncAssertEquals(msg, EntityUtils.toString(e));
			}
		});
		HttpClient c = buildClient(1);
		HttpPut req = new HttpPut("http://localhost:10000/foo/bar?a=b&a=");
		req.setEntity(new StringEntity(msg, mediaType, CS_UTF_8.name()));
		req.addHeader("X-foo","bar");
		c.execute(req);
		server.stop();
		dumpFailures(System.err);
		Assert.assertFalse(isFailed());
	}
	
	@Test
	public void shouldAddCookiesOnServletHttpResponseWhenHeaderSetCookie() throws IOException {
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		HttpResponse response = new BasicHttpResponse(new HttpCoreHelper().statusLine(200));
		Header[] headers = new Header[2];
		headers[0] = new BasicHeader("Set-Cookie", "etalia=dGltZXN0YW1wPTEzMzE4ODQxxNzE4ODc.dXNlcm5hbWU9bHVjYQ.Y3JlYXRlZF9hdD0xxMzMxxODg0MTcxxODg3.cGFzc3dvcmQ9b0Z2WWtNU0dqcUdBZjRWa0JWMGZ1bmZHdW9F.Y29uZmlybV9hY2s9MDg0ZGM3ZTItM2VlMC00NWNkLWI2NmQtZGVmZTlmM2E5NTRm;Domain=localhost;Path=/;Expires=Mon, 02-Apr-2012 10:30:00 GMT");
		headers[1] = new BasicHeader("Set-Cookie", "etalia_hash=4bYEnD7APP19SKPlf1x1IDrPzNY0;Domain=localhost;Path=/;Expires=Mon, 02-Apr-2012 10:30:00 GMT");
		response.setHeaders(headers);
		
		servletHelper.copyResponse(response, servletResponse);
		
		Cookie[] cookies = servletResponse.getCookies();
		
		assertEquals(2, cookies.length);
		
		assertEquals("/", cookies[0].getPath());
		assertEquals("localhost", cookies[0].getDomain());
		assertNotSame(0, cookies[0].getMaxAge());
		
		assertEquals("/", cookies[1].getPath());
		assertEquals("localhost", cookies[1].getDomain());
		assertNotSame(0, cookies[1].getMaxAge());
	}

}
