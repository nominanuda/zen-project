package com.nominanuda.web.http;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.message.LineFormatter;
import org.junit.Test;

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

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
