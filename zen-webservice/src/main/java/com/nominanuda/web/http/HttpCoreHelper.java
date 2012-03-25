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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
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
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.io.IOHelper;
import com.nominanuda.io.OutputStreamWriter;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Exceptions;
import com.nominanuda.lang.ReflectionHelper;

@ThreadSafe
public class HttpCoreHelper implements HttpProtocol {
	private static final HttpRequestFactory httpRequestFactory = new DefaultHttpRequestFactory();
	private static final HttpResponseFactory httpResponseFactory = new DefaultHttpResponseFactory();
	private static final IOHelper IO = new IOHelper();
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
		String charset = null;
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
		&& contentType.equalsIgnoreCase(CT_WWW_FORM_URLENCODED)) {
			final String content = EntityUtils.toString(entity, HTTP.UTF_8);
			if (content != null && content.length() > 0) {
				result = new ArrayList<NameValuePair>();
				URLEncodedUtils.parse(result, new Scanner(content), charset);
			}
		}
		return result;
	}

	public DataStruct<?> getQueryParams(HttpRequest request) {
		List<NameValuePair> l = URLEncodedUtils.parse(
				URI.create(request.getRequestLine().getUri()), UTF_8);
		return toDataStruct(l);
	}
	public DataStruct<?> toDataStruct(List<NameValuePair> l) {
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
		p.setLongParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connTimeoutMillis);
		p.setLongParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeoutMillis);
		HttpClient httpClient = new DefaultHttpClient(cm);
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
}
