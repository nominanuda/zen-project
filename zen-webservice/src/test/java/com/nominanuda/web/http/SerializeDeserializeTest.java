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

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

public class SerializeDeserializeTest {
	private static final String PAYLOAD = "lavispateresa";
	private static final String REQ_URI = "/bar/?baz=BAZ";

	@Test
	public void testRequest() throws IOException, HttpException {
		HttpPost req = new HttpPost(REQ_URI);
		req.addHeader("h1", "v1");
		req.setEntity(new StringEntity(PAYLOAD, ContentType.create("text/plain", "UTF-8")));
		byte[] serialized = HTTP.serialize(req);
		//System.err.println(new String(serialized, "UTF-8"));
		HttpPost m = (HttpPost)HTTP.deserialize(new ByteArrayInputStream(serialized));
		//System.err.println(new String(HTTP.serialize(m), "UTF-8"));
		assertEquals(REQ_URI, m.getRequestLine().getUri());
		assertEquals(PAYLOAD.getBytes(HttpProtocol.CS_UTF_8).length, ((ByteArrayEntity)m.getEntity()).getContentLength());
		assertEquals("v1", m.getFirstHeader("h1").getValue());
	}

	@Test
	public void testResponse() throws IOException, HttpException {
		HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
		resp.addHeader("h1", "v1");
		resp.setEntity(new StringEntity(PAYLOAD, ContentType.create("text/plain", "UTF-8")));
		byte[] serialized = HTTP.serialize(resp);
		//System.err.println(new String(serialized, "UTF-8"));
		HttpResponse m = (HttpResponse)HTTP.deserialize(new ByteArrayInputStream(serialized));
		//System.err.println(new String(HTTP.serialize(m), "UTF-8"));
		assertEquals(200, m.getStatusLine().getStatusCode());
		assertEquals(PAYLOAD.getBytes(HttpProtocol.CS_UTF_8).length, ((ByteArrayEntity)m.getEntity()).getContentLength());
		assertEquals("v1", m.getFirstHeader("h1").getValue());
	}
}
