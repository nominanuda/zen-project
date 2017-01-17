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

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static com.nominanuda.dataobject.WrappingFactory.WF;
import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.JSONParser;
import com.nominanuda.web.http.HttpProtocol;


public class HyperApiWsSkeltonTest {

	@Test
	public void testFoo2() throws Exception {
		HyperApiWsSkelton skelton = new HyperApiWsSkelton();
		skelton.setApi(TestHyperApi2.class);
		skelton.setService(new TestHyperApi2() {
			public DataObject putFoo(String bar, String baz, DataObject foo) {
				return foo;
			}});
		skelton.setRequestUriPrefix("/mytest");
		HttpPut request = new HttpPut("/mytest/foo/BAR?baz=BAZ");
		DataObject foo = new DataObjectImpl();
		foo.put("foo", "FOO");
		request.setEntity(new StringEntity(new DataStructHelper().toJsonString(foo),
			ContentType.create(HttpProtocol.CT_APPLICATION_JSON, HttpProtocol.CS_UTF_8)));
		HttpResponse response = skelton.handle(request);
		DataStruct result = new JSONParser().parse(new InputStreamReader(response.getEntity().getContent()));
		Assert.assertEquals("FOO", ((DataObject)result).get("foo"));
	}

	@Test
	public void testFoo() throws Exception {
		HyperApiWsSkelton skelton = new HyperApiWsSkelton();
		skelton.setApi(TestHyperApi.class);
		skelton.setService(new TestHyperApi() {
			@Override
			public Boo putFoo(String bar, String baz, Moo moo) {
//TODO				assertEquals("miao", moo.miao());
				return WF.wrap(STRUCT.newObject().with("AA", "BB"), Boo.class);
			}
		});
		skelton.setRequestUriPrefix("/mytest");
		HttpPut request = new HttpPut("/mytest/foo/BAR?baz=BAZ");
		DataObject foo = new DataObjectImpl();
		foo.put("foo", "FOO");
		request.setEntity(new StringEntity(new DataStructHelper().toJsonString(foo),
			ContentType.create(HttpProtocol.CT_APPLICATION_JSON, HttpProtocol.CS_UTF_8)));
		HttpResponse response = skelton.handle(request);
		DataStruct result = new JSONParser().parse(new InputStreamReader(response.getEntity().getContent()));
		Assert.assertEquals("BB", ((DataObject)result).get("AA"));
	}

}
