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

import static com.nominanuda.zen.obj.wrap.Wrap.WF;
import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.hyperapi.TestHyperApi.Boo;
import com.nominanuda.hyperapi.TestHyperApi.Moo;
import com.nominanuda.web.http.BaseHttpTest;
import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.zen.obj.Obj;


public class HttpClientHyperApiFactoryTest extends BaseHttpTest {

	@Test
	public void testE() throws Exception {
		Server s =  startServer(12000, new AbstractHandler() {
			public void handle(String uri, Request arg1, HttpServletRequest req,
					HttpServletResponse resp) throws IOException, ServletException {
				IO.pipe(req.getInputStream(), resp.getOutputStream(), false, false);
			}
		});
		HttpCoreHelper httpCoreHelper = new HttpCoreHelper();
		HttpClient client = httpCoreHelper.createClient(10, 100000000, 100000000);
		HttpClientHyperApiFactory f = new HttpClientHyperApiFactory();
		f.setHttpClient(client);
		f.setUriPrefix("http://localhost:12000");
		TestHyperApi2 api = f.getInstance("", TestHyperApi2.class);
		Obj foo = Obj.make();
		foo.put("foo", "FOO");
		Obj result = api.putFoo("BAR", "BAZ", foo);
		Assert.assertEquals("FOO", result.get("foo"));
		s.stop();
		s.destroy();
	}

	@Test
	public void testWrapping() throws Exception {
		Server s =  startServer(12000, new AbstractHandler() {
			public void handle(String uri, Request arg1, HttpServletRequest req,
					HttpServletResponse resp) throws IOException, ServletException {
				resp.setHeader("Content-Type", "application/json");
				IO.pipe(req.getInputStream(), resp.getOutputStream(), false, false);
			}
		});
		HttpCoreHelper httpCoreHelper = new HttpCoreHelper();
		HttpClient client = httpCoreHelper.createClient(10, 100000000, 100000000);
		HttpClientHyperApiFactory f = new HttpClientHyperApiFactory();
		f.setHttpClient(client);
		f.setUriPrefix("http://localhost:12000");
		TestHyperApi api = f.getInstance("", TestHyperApi.class);
		
		Obj foo = Obj.make();
		foo.put("foo", "FOO");
		Moo moo = WF.wrap(foo, Moo.class);
		Boo result = api.putFoo("BAR", "BAZ", moo);
		Assert.assertEquals("FOO", result.unwrap().get("foo"));
		s.stop();
		s.destroy();
	}
}
