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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.io.IOHelper;
import com.nominanuda.web.mvc.WebService;

public class PayloadCodecTest {
	private static final String HTTP_LOCALHOST_12000 = "http://localhost:12000";

	@Test
	public void testTestHyperApi() {
		HyperApiWsSkelton skelton = makeSkelton(TestHyperApi.class, new TestHyperApi() {
			public DataObject putFoo(String bar, String baz, DataObject foo) {
				return foo;
			}});
		TestHyperApi api = makeStub(skelton, TestHyperApi.class);
		DataObject o = api.putFoo("BAR", "BAZ", new DataObjectImpl().with("x", "y"));
		assertEquals("y", o.get("x"));
	}

	@Test
	public void testApi1() throws IOException {
		HyperApiWsSkelton skelton = makeSkelton(Api1.class, new Api1() {
			public InputStream put(byte[] foo) {
				return new ByteArrayInputStream(foo);
			}});
		Api1 api = makeStub(skelton, Api1.class);
		byte[] o = IOHelper.IO.readAndClose(api.put("BAR".getBytes()));
		assertArrayEquals("BAR".getBytes(), o);
	}

	@HyperApi
	interface Api1 {
		@PUT @Path("/")
		InputStream put(byte[] foo);
	}

	@Test
	public void testApi2() {
		HyperApiWsSkelton skelton = makeSkelton(Api2.class, new Api2() {
			public byte[] put(InputStream foo) {
				try {
					return IOHelper.IO.readAndClose(foo);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}});
		Api2 api = makeStub(skelton, Api2.class);
		byte[] o = api.put(new ByteArrayInputStream("BAR".getBytes()));
		assertArrayEquals("BAR".getBytes(), o);
	}

	@HyperApi
	interface Api2 {
		@PUT @Path("/")
		byte[] put(InputStream foo);
	}

	@Test
	public void testAnyJsonApi() {
		HyperApiWsSkelton skelton = makeSkelton(Api3.class, new Api3() {
			public String x(String x) { return x; }
			public Boolean y(Boolean y) { return y; }
			public Double w(Double w) { return w; }
			public Long k(Long k) { return k; }
		});
		Api3 api = makeStub(skelton, Api3.class);
		assertEquals("B", api.x("B"));
		assertEquals("", api.x(""));
		assertEquals(null, api.x(null));
		assertEquals(null, api.y(null));
		assertEquals(null, api.w(null));
		assertEquals(null, api.k(null));
		assertEquals(new Long(1), api.k(1l));
		assertEquals(new Boolean(false), api.y(false));
		assertEquals(new Double(1.1), api.w(1.1));
	}

	@HyperApi
	interface Api3 {
		@PUT @Path("/x")String x(String x);
		@PUT @Path("/y")Boolean y(Boolean y);
		@PUT @Path("/w")Double w(Double w);
		@PUT @Path("/k")Long k(Long k);
	}

	private <T> HyperApiWsSkelton makeSkelton(Class<T> api, T impl) {
		HyperApiWsSkelton skelton = new HyperApiWsSkelton();
		skelton.setApi(api);
		skelton.setService(impl);
		skelton.setRequestUriPrefix(HTTP_LOCALHOST_12000);
		return skelton;
	}

	private <T> T makeStub(HyperApiWsSkelton skelton, Class<T> api) {
		HttpClient client = new InMemoryClient(skelton);
		HttpClientHyperApiFactory f = new HttpClientHyperApiFactory();
		f.setHttpClient(client);
		f.setUriPrefix(HTTP_LOCALHOST_12000);
		T apii = f.getInstance("", api);
		return apii;
	}

	private class InMemoryClient implements HttpClient {
		private final WebService ws;
		public InMemoryClient(WebService ws) {
			this.ws = ws;
		}
		public HttpResponse execute(HttpUriRequest request) throws IOException,
				ClientProtocolException {
			try {
				return ws.handle(request);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		public HttpParams getParams() {
			return null;
		}
		public ClientConnectionManager getConnectionManager() {
			return null;
		}
		public HttpResponse execute(HttpUriRequest request, HttpContext context)
				throws IOException, ClientProtocolException {
			return null;
		}
		public HttpResponse execute(HttpHost target, HttpRequest request)
				throws IOException, ClientProtocolException {
			return null;
		}
		public HttpResponse execute(HttpHost target, HttpRequest request,
				HttpContext context) throws IOException,
				ClientProtocolException {
			return null;
		}
		public <T> T execute(HttpUriRequest request,
				ResponseHandler<? extends T> responseHandler)
				throws IOException, ClientProtocolException {
			return null;
		}
		public <T> T execute(HttpUriRequest request,
				ResponseHandler<? extends T> responseHandler,
				HttpContext context) throws IOException,
				ClientProtocolException {
			return null;
		}
		public <T> T execute(HttpHost target, HttpRequest request,
				ResponseHandler<? extends T> responseHandler)
				throws IOException, ClientProtocolException {
			return null;
		}
		public <T> T execute(HttpHost target, HttpRequest request,
				ResponseHandler<? extends T> responseHandler,
				HttpContext context) throws IOException,
				ClientProtocolException {
			return null;
		}
	}
}
