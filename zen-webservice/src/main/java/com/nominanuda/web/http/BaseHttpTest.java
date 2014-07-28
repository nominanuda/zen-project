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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.nominanuda.io.IOHelper;

public abstract class BaseHttpTest implements HttpProtocol {
	protected HttpCoreHelper httpCoreHelper = new HttpCoreHelper();
	protected IOHelper io = new IOHelper();
	protected int maxConnPerRoute = 10;
	private HttpClient client;
	private final List<String> failures = Collections.synchronizedList(new LinkedList<String>());

	protected void asyncAssertEquals(Object o1, Object o2) {
		if(o1 == null || o2 == null) {
			asyncFail("null object on equals");
		} else {
			asyncAssert(o1.equals(o2), "expected "+o1.toString()+" got "+o2.toString());
		}
	}
	protected void asyncAssert(boolean cond) {
		if(! cond) {
			asyncFail();
		}
	}
	protected void asyncAssert(boolean cond, String reason) {
		if(! cond) {
			asyncFail(reason);
		}
	}
	protected void asyncFail() {
		failures.add("");
	}
	protected void asyncFail(String reason) {
		failures.add(reason);
	}
	protected boolean isFailed() {
		return ! failures.isEmpty();
	}
	protected void dumpFailures(OutputStream os) throws IOException {
		dumpFailures(new OutputStreamWriter(os, CS_UTF_8));
	}
	protected void dumpFailures(Writer w) throws IOException {
		for(String f : failures) {
			w.write("failure:"+f+"\n");
		}
		w.flush();
	}
	protected Server startServer(int port, final Handler h) throws Exception {
		Server srv = new Server(port);
		srv.setHandler(new AbstractHandler() {
			public void handle(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				h.handle(target, baseRequest, request, response);
				baseRequest.setHandled(true);
			}});
		srv.start();
		return srv;
	}


	protected HttpClient buildClient(int maxConn) {
		return httpCoreHelper.createClient(maxConnPerRoute, 100000000, 100000000);
	}

	protected boolean GETfor200(URI uri) throws ClientProtocolException, IOException {
		HttpResponse resp = GET(uri);
		HttpEntity entity = resp.getEntity();
		try {
			return (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode());
		} finally {
			EntityUtils.consume(entity);
		}
	}

	protected HttpResponse GET(URI uri) throws ClientProtocolException, IOException {
		if(client == null) {
			client = buildClient(maxConnPerRoute);
		}
		return client.execute(new HttpGet(uri));
	}

	protected HttpResponse POST(URI uri, String ct, byte[] body) throws ClientProtocolException, IOException {
		return performEntityEnclosingMethod(ct, body, new HttpPost(uri));
	}

	protected HttpResponse performEntityEnclosingMethod(String ct, byte[] body,
			HttpEntityEnclosingRequestBase post) throws IOException, ClientProtocolException {
		if(client == null) {
			client = buildClient(maxConnPerRoute);
		}
		ByteArrayEntity entity = new ByteArrayEntity(body);
		entity.setContentType(ct);
		post.setEntity(entity);
		return client.execute(post);
	}

	protected HttpResponse PUT(URI uri, String ct, byte[] body) throws ClientProtocolException, IOException {
		return performEntityEnclosingMethod(ct, body, new HttpPut(uri));
	}

	protected byte[] getBody(HttpResponse resp) throws ClientProtocolException, IOException {
		HttpEntity entity = resp.getEntity();
		try {
			if(HttpStatus.SC_OK != resp.getStatusLine().getStatusCode()) {
				throw new RuntimeException();
			}
			return io.readAndClose(entity.getContent());
		} finally {
			EntityUtils.consume(entity);
		}
	}

	protected String asString(HttpResponse resp) throws ClientProtocolException, IOException {
		HttpEntity entity = resp.getEntity();
		try {
			if(HttpStatus.SC_OK != resp.getStatusLine().getStatusCode()) {
				throw new RuntimeException();
			}
			Charset cs = ContentType.get(entity).getCharset();
			return new String(io.readAndClose(entity.getContent()), cs == null ? CS_UTF_8 : cs);
		} finally {
			EntityUtils.consume(entity);
		}
	}
}
