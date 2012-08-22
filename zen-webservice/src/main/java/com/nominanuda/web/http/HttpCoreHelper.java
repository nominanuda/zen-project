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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProxySelector;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.NetscapeDraftSpec;
import org.apache.http.impl.cookie.RFC2109Spec;
import org.apache.http.impl.cookie.RFC2965Spec;
import org.apache.http.impl.io.AbstractMessageWriter;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.impl.io.AbstractSessionOutputBuffer;
import org.apache.http.impl.io.HttpRequestParser;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.impl.io.HttpResponseParser;
import org.apache.http.impl.io.HttpResponseWriter;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Exceptions;
import com.nominanuda.lang.ReflectionHelper;
import com.nominanuda.lang.Strings;

import static com.nominanuda.io.IOHelper.IO;

@ThreadSafe
public class HttpCoreHelper implements HttpProtocol {
	public static final HttpCoreHelper HTTP = new HttpCoreHelper();

	private static final HttpRequestFactory httpRequestFactory = new DefaultHttpRequestFactory();
	private static final HttpResponseFactory httpResponseFactory = new DefaultHttpResponseFactory();
	private static final ReflectionHelper reflect = new ReflectionHelper();

	public final ProtocolVersion ProtocolVersion_1_1 = new ProtocolVersion(
			"HTTP", 1, 1);

	public HttpRequest deserializeRequest(InputStream is) throws IOException,
			HttpException {
		byte[] buf = IO.readAndClose(is);
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		BufferedSessionInputBuffer sessionInputBuffer = new BufferedSessionInputBuffer(
				bais);
		HttpRequestParser messageParser = new HttpRequestParser(
				sessionInputBuffer, new BasicLineParser(), httpRequestFactory,
				new BasicHttpParams());
		return (HttpRequest) messageParser.parse();
	}

	public HttpResponse deserializeResponse(InputStream is) throws IOException,
			HttpException {
		byte[] buf = IO.readAndClose(is);
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		BufferedSessionInputBuffer sessionInputBuffer = new BufferedSessionInputBuffer(
				bais);
		HttpResponseParser messageParser = new HttpResponseParser(
				sessionInputBuffer, new BasicLineParser(), httpResponseFactory,
				new BasicHttpParams());
		HttpResponse resp = (HttpResponse) messageParser.parse();
		byte[] bb = serialize(resp);
		if (buf.length > bb.length) {
			byte[] tmp = new byte[buf.length - bb.length];
			System.arraycopy(buf, bb.length, tmp, 0, buf.length - bb.length);
			resp.setEntity(new ByteArrayEntity(tmp));
		}
		return resp;
	}

	public byte[] serialize(HttpMessage message) throws IOException,
			HttpException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedSessionOutputBuffer sob = new BufferedSessionOutputBuffer(baos);
		AbstractMessageWriter messageWriter = message instanceof HttpRequest ? new HttpRequestWriter(
				sob, new BasicLineFormatter(), new BasicHttpParams())
				: new HttpResponseWriter(sob, new BasicLineFormatter(),
						new BasicHttpParams());
		messageWriter.write(message);
		sob.flush();
		HttpEntity entity = message instanceof HttpEntityEnclosingRequest ? ((HttpEntityEnclosingRequest) message)
				.getEntity()
				: message instanceof HttpResponse ? ((HttpResponse) message)
						.getEntity() : null;
		if (entity != null) {
			// TODO pipe ??
			baos.write(IO.readAndClose(entity.getContent()));
		}
		return baos.toByteArray();
	}

	private class BufferedSessionOutputBuffer extends
			AbstractSessionOutputBuffer {
		public BufferedSessionOutputBuffer(OutputStream outstream) {
			super.init(outstream, 4096, new BasicHttpParams());
		}
	}

	private class BufferedSessionInputBuffer extends AbstractSessionInputBuffer {
		private ByteArrayInputStream bis;

		public BufferedSessionInputBuffer(ByteArrayInputStream is) {
			bis = is;
			super.init(is, 4096, new BasicHttpParams());
		}

		public boolean isDataAvailable(int timeout) throws IOException {
			return bis.available() > 0;
		}
	}

	public StatusLine statusLine(int code) {
		Check.illegalargument.assertTrue(code > 99 && code < 600);
		String reason = Check.ifNull(HttpProtocol.statusToReason.get(code), "");
		return new BasicStatusLine(ProtocolVersion_1_1, code, reason);
	}

	public void setContentType(HttpResponse resp, String contentType) {
		HttpEntity e = resp.getEntity();
		if(e != null && e instanceof AbstractHttpEntity) {
			AbstractHttpEntity ae = (AbstractHttpEntity)e;
			ae.setContentType(contentType);
		} else {
			resp.setHeader(HDR_CONTENT_TYPE, contentType);
		}
	}

	public void setContentEncoding(HttpResponse resp, String contentEncoding) {
		HttpEntity e = resp.getEntity();
		if(e != null && e instanceof AbstractHttpEntity) {
			AbstractHttpEntity ae = (AbstractHttpEntity)e;
			ae.setContentEncoding(contentEncoding);
		} else {
			resp.setHeader(HDR_CONTENT_ENCODING, contentEncoding);
		}
	}

	public void setContentLength(HttpResponse resp, int contentLength) {
		resp.setHeader(HDR_CONTENT_LENGTH, Integer.valueOf(contentLength)
				.toString());
	}

	public @Nullable String guessCharset(HttpEntity entity) {
		Header h = entity.getContentType();
		return h == null ? null : guessCharset(h);
	}

	public @Nullable String guessCharset(String contentType) {
		Header h = new BasicHeader(HDR_CONTENT_TYPE, contentType);
		return guessCharset(h);
	}

	private @Nullable String guessCharset(Header h) {
		HeaderElement[] elems = h.getElements();
		if (elems.length > 0) {
			HeaderElement elem = elems[0];
			NameValuePair param = elem.getParameterByName("charset");
			if (param != null) {
				return param.getValue();
			}
		}
		return null;
	}

	public List<NameValuePair> parseEntityWithDefaultUtf8(
			final HttpEntity entity) throws IOException {
		List<NameValuePair> result = Collections.emptyList();
		String contentType = null;
		String charset = UTF_8;
		Header h = entity.getContentType();
		if (h != null) {
			HeaderElement[] elems = h.getElements();
			if (elems.length > 0) {
				HeaderElement elem = elems[0];
				contentType = elem.getName();
				NameValuePair param = elem.getParameterByName("charset");
				if (param != null) {
					charset = param.getValue();
				}
			}
		}
		if (contentType != null
		&& contentType.trim().toLowerCase().startsWith(CT_WWW_FORM_URLENCODED.toLowerCase())) {
			final String content = EntityUtils.toString(entity, UTF_8);
			if (content != null && content.length() > 0) {
				result = new ArrayList<NameValuePair>();
				URLEncodedUtils.parse(result, new Scanner(content), charset);
			}
		}
		return result;
	}

	public @Nullable String getQueryParamFirstOccurrence(HttpRequest request, String name) {
		List<NameValuePair> l = URLEncodedUtils.parse(
				URI.create(request.getRequestLine().getUri()), UTF_8);
		for(NameValuePair nvp : l) {
			if(name.equals(nvp.getName())) {
				return nvp.getValue();
			}
		}
		return null;
	}
	public DataStruct getQueryParams(HttpRequest request) {
		List<NameValuePair> l = URLEncodedUtils.parse(
				URI.create(request.getRequestLine().getUri()), UTF_8);
		return toDataStruct(l);
	}
	public DataStruct toDataStruct(List<NameValuePair> l) {
		DataObjectImpl res = new DataObjectImpl();
		for(NameValuePair nvp : l) {
			res.setOrPushPathProperty(nvp.getName(), nvp.getValue());
		}
		return res;

	}
	public HttpResponse createBasicResponse(int status) {
		StatusLine statusline = statusLine(status);
		HttpResponse resp = new BasicHttpResponse(statusline);
		return resp;
	}
	public HttpResponse redirectTo(String url) {
		StatusLine statusline = statusLine(302);
		HttpResponse resp = new BasicHttpResponse(statusline);
		resp.addHeader(HDR_LOCATION, url);
		return resp;
	}

	public boolean hasEntity(HttpMessage message) {
		return
		Check.notNull(message) instanceof HttpResponse
			? ((HttpResponse)message).getEntity() != null
			: message instanceof HttpEntityEnclosingRequest
				? ((HttpEntityEnclosingRequest)message).getEntity() != null
				: false;
	}

	public HttpResponse resp404TextPlainUtf8(String msg) {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(404));
		try {
			resp.setEntity(new StringEntity(msg, CT_TEXT_PLAIN, UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return resp;
	}

	public HttpResponse resp500TextPlainUtf8(Exception e) {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(500));
		try {
			resp.setEntity(new StringEntity(
				Exceptions.toStackTrace(e), CT_TEXT_PLAIN, UTF_8));
		} catch (UnsupportedEncodingException ex) {}
		return resp;
	}

	public HttpResponse respInternalServerError() {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(500));
		try {
			resp.setEntity(new StringEntity(
				"Internal Server Error", CT_TEXT_PLAIN, UTF_8));
		} catch (UnsupportedEncodingException ex) {}
		return resp;
	}

	public HttpResponse createBasicResponse(int status, String message,
			String contentType) {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(status));
		try {
			HttpCoreHelper d = new HttpCoreHelper();
			String charset = Check.ifNullOrBlank(d.guessCharset(contentType), "UTF-8");
			HttpEntity e = new StringEntity(
					message, contentType, charset);
			resp.setEntity(e);
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalArgumentException(ex);
		}
		return resp;
	}

	public boolean isMultipart(HttpEntity entity) {
		return Check.notNull(entity) instanceof MultipartEntity;
	}

	public HttpMultipart extractHttpMultipart(MultipartEntity entity) {
		HttpMultipart mp = (HttpMultipart)reflect.getFieldValue("multipart", entity, true);
		return Check.notNull(mp);
	}

	public ContentBody extractFirstPartBody(MultipartEntity entity) {
		HttpMultipart mp = extractHttpMultipart(entity);
		FormBodyPart part = mp.getBodyParts().get(0);
		ContentBody cb = part.getBody();
		return cb;
	}
	

	public HttpClient createClient(int maxConnPerRoute, long connTimeoutMillis, long soTimeoutMillis) {
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(maxConnPerRoute );
		cm.setDefaultMaxPerRoute(maxConnPerRoute);
		HttpParams p = new BasicHttpParams();
		p.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Long(connTimeoutMillis).intValue());
		p.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, new Long(soTimeoutMillis).intValue());
		HttpClient httpClient = new DefaultHttpClient(cm, p);
		return httpClient;
	}

	//if proxyHostAnPort value is jvm the normal jvm settings apply
	public HttpClient createClient(int maxConnPerRoute, long connTimeoutMillis, long soTimeoutMillis, String proxyHostAnPort) {
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(maxConnPerRoute );
		cm.setDefaultMaxPerRoute(maxConnPerRoute);
		HttpParams p = new BasicHttpParams();
		p.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Long(connTimeoutMillis).intValue());
		p.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, new Long(soTimeoutMillis).intValue());
		DefaultHttpClient httpClient = new DefaultHttpClient(cm, p);
		if("jvm".equalsIgnoreCase(proxyHostAnPort)) {
			ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
					httpClient.getConnectionManager().getSchemeRegistry(),
					ProxySelector.getDefault());
			httpClient.setRoutePlanner(routePlanner);
		} else {
			String[] hostAndPort = proxyHostAnPort.split(":");
			Check.illegalargument.assertTrue(hostAndPort.length < 3, "wrong hostAndPort:"+proxyHostAnPort);
			String host = hostAndPort[0];
			int port = 80;
			if(hostAndPort.length > 1) {
				port = Integer.valueOf(hostAndPort[1]);
			}
			HttpHost proxy = new HttpHost(host, port);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return httpClient;
	}

	public @Nullable HttpEntity getEntity(HttpMessage msg) {
		if(! hasEntity(msg)) {
			return null;
		} else if(msg instanceof HttpResponse) {
			return ((HttpResponse)msg).getEntity();
		} else {
			return ((HttpEntityEnclosingRequest)msg).getEntity();
		}
	}
	//Cookies....
	public enum CookieSpecKind {
		rfc2965Spec(new RFC2965Spec()), 
		rfc2109Spec(new RFC2109Spec()), 
		browserCompat(new BrowserCompatSpec()), 
		netscape(new NetscapeDraftSpec());

		private CookieSpec cookieSpec;

		CookieSpecKind(CookieSpec spec) {
			cookieSpec = spec;
		}

		CookieSpec getSpec() {
			return cookieSpec;
		}
	}

	public List<Cookie> getRequestCookiesByName(HttpRequest req, String name) {
		final HeaderIterator iterator = req.headerIterator();
		List<Cookie> cookies = new LinkedList<Cookie>();
		while (iterator.hasNext()) {
			Header header = iterator.nextHeader();
			if ("Cookie".equals(header.getName())) {
				String remaining = header.getValue();
				List<String> pairs = Strings.splitAndTrim(remaining, ";");
				for (String nav : pairs) {
					int w = nav.indexOf('=');
					if (w > 0) {
						String cname = nav.substring(0, w);
						if (name.equals(cname)) {
							cookies.add(new NameValueCookie(cname, nav
									.substring(w)));
						}
					}
				}
			}
		}
		return cookies;
	}

	public void setResponseCookie(HttpResponse resp, Cookie cookie, CookieSpecKind cookieSpec) {
		Header h = cookieSpec.getSpec().formatCookies(Arrays.asList(cookie)).get(0);
		resp.addHeader("Set-Cookie", h.getValue());

	}

	public void setResponseCookie(HttpResponse req, String name, String value, CookieSpecKind cookieSpec) {
		BasicClientCookie c = new BasicClientCookie(name, value);
		setResponseCookie(req, c, cookieSpec);
	}
}
