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

import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;


public class HyperApiWsSkeltonTest {

	@Test
	public void testFoo2() throws Exception {
		HyperApiWsSkelton skelton = new HyperApiWsSkelton();
		skelton.setApi(TestHyperApi2.class);
		skelton.setService(new TestHyperApi2() {
			public Obj putFoo(String bar, String baz, Obj foo) {
				return foo;
			}});
		skelton.setRequestUriPrefix("/mytest");
		HttpPut request = new HttpPut("/mytest/foo/BAR?baz=BAZ");
		Obj foo = Obj.make();
		foo.put("foo", "FOO");
		request.setEntity(new StringEntity(foo.toString(),
			ContentType.create(HttpProtocol.CT_APPLICATION_JSON, HttpProtocol.CS_UTF_8)));
		HttpResponse response = skelton.handle(request);
		Stru result = Stru.parse(new InputStreamReader(response.getEntity().getContent()));
		Assert.assertEquals("FOO", ((Obj)result).get("foo"));
	}

	@Test
	public void testFoo() throws Exception {
		HyperApiWsSkelton skelton = new HyperApiWsSkelton();
		skelton.setApi(TestHyperApi.class);
		skelton.setService(new TestHyperApi() {
			@Override
			public Boo putFoo(String bar, String baz, Moo moo) {
//TODO				assertEquals("miao", moo.miao());
				return WF.wrap(Obj.make("AA", "BB"), Boo.class);
			}
		});
		skelton.setRequestUriPrefix("/mytest");
		HttpPut request = new HttpPut("/mytest/foo/BAR?baz=BAZ");
		Obj foo = Obj.make();
		foo.put("foo", "FOO");
		request.setEntity(new StringEntity(foo.toString(),
			ContentType.create(HttpProtocol.CT_APPLICATION_JSON, HttpProtocol.CS_UTF_8)));
		HttpResponse response = skelton.handle(request);
		Stru result = Stru.parse(new InputStreamReader(response.getEntity().getContent()));
		Assert.assertEquals("BB", ((Obj)result).get("AA"));
	}

}
