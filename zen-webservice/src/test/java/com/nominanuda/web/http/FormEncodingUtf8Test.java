package com.nominanuda.web.http;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

public class FormEncodingUtf8Test implements HttpProtocol {
	HttpCoreHelper h = new HttpCoreHelper();

	@Test
	public void test() throws IOException {
		String ori = "èè";
		assertEquals(ori, g(URLEncoder.encode(ori, "UTF-8"), CT_WWW_FORM_URLENCODED_CS_UTF8));
		assertEquals(ori, g(URLEncoder.encode(ori, "ISO-8859-1"), CT_WWW_FORM_URLENCODED+";charset=ISO-8859-1"));
		assertEquals(ori, g(URLEncoder.encode(ori, "UTF-8"), CT_WWW_FORM_URLENCODED));
		assertEquals(ori, g1(ori.getBytes(CS_UTF_8), CT_WWW_FORM_URLENCODED_CS_UTF8));
		assertEquals(ori, g1(ori.getBytes("ISO-8859-1"), CT_WWW_FORM_URLENCODED+";charset=ISO-8859-1"));
	}
	private String g(String v, String ct) throws IOException {
		StringEntity se = new StringEntity("foo="+v);
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
