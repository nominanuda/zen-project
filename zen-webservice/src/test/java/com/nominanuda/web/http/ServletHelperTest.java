package com.nominanuda.web.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

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

}
