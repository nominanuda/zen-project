package com.nominanuda.web.http;

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.lang.Tuple2;

public class BasicAuthTest {
	
	@Test
	public void test() {
		String name = "test@test.com";
		String pass = "823jkaoja|#i:01";
		
		Tuple2<String, String> credentials = HTTP.extractBasicAuth(HTTP.basicAuthHeader(name, pass));
		Assert.assertNotNull(credentials);
		Assert.assertEquals(credentials.get0(), name);
		Assert.assertEquals(credentials.get1(), pass);
	}
}
