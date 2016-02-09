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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Ignore;
import org.junit.Test;

public class FormEncodingUtf8Test implements HttpProtocol {
	HttpCoreHelper h = new HttpCoreHelper();

	@Test @Ignore
	public void test() throws IOException {
		String ori = "è è";
		assertEquals(ori, g(URLEncoder.encode(ori, "UTF-8"), CT_WWW_FORM_URLENCODED_CS_UTF8));
		assertEquals(ori, g(URLEncoder.encode(ori, "ISO-8859-1"), CT_WWW_FORM_URLENCODED+";charset=ISO-8859-1"));
		assertEquals(ori, g(URLEncoder.encode(ori, "UTF-8"), CT_WWW_FORM_URLENCODED));
		assertEquals(ori, g1(ori.getBytes(CS_UTF_8), CT_WWW_FORM_URLENCODED_CS_UTF8));
		assertEquals(ori, g1(ori.getBytes("ISO-8859-1"), CT_WWW_FORM_URLENCODED+";charset=ISO-8859-1"));
	}
	private String g(String v, String ct) throws IOException {
		StringEntity se = new StringEntity("f="+v);
		if(ct != null) {
			se.setContentType(ct);
		}
		List<NameValuePair> pairs = h.parseEntityWithDefaultUtf8(se);
		String vv = pairs.get(0).getValue();
		return vv;
	}
	private String g1(byte[] v, String ct) throws IOException {
		byte[] x = new byte[2+v.length];
		x[0] = 'f';
		x[1] = '=';
		System.arraycopy(v, 0, x, 2, v.length);
		ByteArrayEntity se = new ByteArrayEntity(x);
		if(ct != null) {
			se.setContentType(ct);
		}
		List<NameValuePair> pairs = h.parseEntityWithDefaultUtf8(se);
		String vv = pairs.get(0).getValue();
		return vv;
	}
}
