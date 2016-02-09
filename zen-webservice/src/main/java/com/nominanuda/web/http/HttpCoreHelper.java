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

import static com.nominanuda.io.IOHelper.IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.NetscapeDraftSpec;
import org.apache.http.impl.cookie.RFC2109Spec;
import org.apache.http.impl.cookie.RFC2965Spec;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.message.LineFormatter;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.codec.Base64Codec;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Collections;
import com.nominanuda.lang.Exceptions;
import com.nominanuda.lang.ReflectionHelper;
import com.nominanuda.lang.Strings;

@ThreadSafe
public class HttpCoreHelper implements HttpProtocol {
	public static final HttpCoreHelper HTTP = new HttpCoreHelper();

	private static final ReflectionHelper reflect = new ReflectionHelper();
	private final static byte CR = 13;/*US-ASCII CR carriage return*/
	private final static byte LF = 10;/*US-ASCII LF linefeed*/
	private static final byte[] CRLF = new byte[] {CR, LF};
	private static final LineFormatter LINE_FORMATTER = BasicLineFormatter.INSTANCE;
	private static final LineParser LINE_PARSER = BasicLineParser.INSTANCE;
	private final static Pattern MULTIPART_NAME_RE = Pattern.compile("^.*\\bname=\"([^\"]+)\";?.*$");
	private final Base64Codec base64 = new Base64Codec();
	
	public final ProtocolVersion ProtocolVersion_1_1 = new ProtocolVersion("HTTP", 1, 1);

	public HttpMessage deserialize(InputStream is) throws IOException, HttpException {
		PushbackInputStream pis = new PushbackInputStream(is, 4);
		byte[] bb = new byte[4];
		Check.illegalargument.assertTrue(4 == pis.read(bb), "premature end of stream");
		pis.unread(bb);
		return bb[0] == 'H' && bb[1] == 'T' && bb[2] == 'T' && bb[3] == 'P'
			? deserializeResponse(pis)
			: deserializeRequest(pis);
	}

	private HttpRequest deserializeRequest(InputStream is) throws IOException, HttpException {
		CharArrayBuffer lineBuf = readLine(is);
		final ParserCursor cursor = new ParserCursor(0, lineBuf.length());
		RequestLine requestline = LINE_PARSER.parseRequestLine(lineBuf, cursor);
		HttpRequest req = createRequest(requestline.getMethod(), requestline.getUri());
		fillMessageHeadersAndContent(req, is);
		return req;
	}

	private HttpResponse deserializeResponse(InputStream is) throws IOException, HttpException {
		CharArrayBuffer lineBuf = readLine(is);
		final ParserCursor cursor = new ParserCursor(0, lineBuf.length());
		StatusLine requestline = LINE_PARSER.parseStatusLine(lineBuf, cursor);
		HttpResponse resp = createBasicResponse(requestline.getStatusCode(), requestline.getReasonPhrase());
		fillMessageHeadersAndContent(resp, is);
		return resp;
	}

	private void fillMessageHeadersAndContent(HttpMessage msg, InputStream is) throws IOException, HttpException {
		CharArrayBuffer lineBuf = null;
		long cl = 0;
		Header ct = null;
		Header ce = null;
		while((lineBuf = readLine(is)).length() > 0) {
			Header h = LINE_PARSER.parseHeader(lineBuf);
			String hn = h.getName();
			if(HDR_CONTENT_LENGTH.equalsIgnoreCase(hn)) {
				cl = Long.valueOf(h.getValue());
			} else if(HDR_CONTENT_TYPE.equalsIgnoreCase(hn)) {
				ct = h;
			} else if(HDR_CONTENT_ENCODING.equalsIgnoreCase(hn)) {
				ce = h;
			} else {
				msg.addHeader(h);
			}
		}
		if(cl > 0) {
			byte[] payload = IO.readAndClose(is);
			Check.runtime.assertTrue(cl == payload.length);
			ByteArrayEntity bae = new ByteArrayEntity(payload);
			if(ct != null) {
				bae.setContentType(ct);
			}
			if(ce != null) {
				bae.setContentEncoding(ce);
			}
			setEntity(msg, bae);
		}
	}

	private void setEntity(HttpMessage msg, HttpEntity entity) {
		if(Check.notNull(msg) instanceof HttpResponse) {
			((HttpResponse)msg).setEntity(entity);
		} else {
			((HttpEntityEnclosingRequest)msg).setEntity(entity);
		}
	}

	public HttpRequest createRequest(String method, String url) {
		if (GET.equals(method)) {
			return new HttpGet(url);
		} else if (POST.equals(method)) {
			return new HttpPost(url);
		} else if (PUT.equals(method)) {
			return new HttpPut(url);
		} else if (Collections.find(method, RFC2616_SPECIAL_METHODS)) {
			return new BasicHttpRequest(method, url);
		} else {
			throw new IllegalArgumentException(method + " method not supported");
		}
	}

	private CharArrayBuffer readLine(InputStream is) throws IOException {
		CharArrayBuffer cab = new CharArrayBuffer(128);
		int status = 0;//0 reading, 1 CR seen
		while(true) {
			char c = (char)is.read();
			switch (c) {
			case (char)-1:
				throw new IOException("premature end of stream");
			case CR:
				if(status == 1) {
					cab.append(c);
				}
				status = 1;
				break;
			case LF:
				if(status == 1) {
					return cab;
				} else {
					cab.append(LF);
				}
				break;
			default:
				cab.append(c);
				break;
			}
		}
	}

	public byte[] serialize(HttpMessage message) throws IOException,
			HttpException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializeTo(message, baos);
		return baos.toByteArray();
	}

	public void serializeTo(HttpMessage message, OutputStream os)
			throws IOException, HttpException {
		String hl = null;
		HttpEntity entity = null;
		if (message instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) message;
			hl = new String(LINE_FORMATTER.formatRequestLine(null,
					req.getRequestLine()).toCharArray());
			if (req instanceof HttpEntityEnclosingRequest) {
				entity = ((HttpEntityEnclosingRequest) req).getEntity();
			}
		} else {
			HttpResponse resp = (HttpResponse) message;
			hl = new String(LINE_FORMATTER.formatStatusLine(null,
					resp.getStatusLine()).toCharArray());
			entity = resp.getEntity();
		}
		serializeInternal(hl, message.headerIterator(), entity, os);
	}

	private void serializeInternal(String headLine, HeaderIterator hi, @Nullable HttpEntity entity, OutputStream os) throws IOException, HttpException {
		os.write(headLine.getBytes(CS_ASCII));
		os.write(CRLF);
		for(final HeaderIterator it = hi; it.hasNext();) {
			final Header header = it.nextHeader();
			String hn = header.getName();
			if(HDR_CONTENT_LENGTH.equalsIgnoreCase(hn)
			|| HDR_CONTENT_ENCODING.equalsIgnoreCase(hn)
			|| HDR_CONTENT_TYPE.equalsIgnoreCase(hn)
			) {
				continue;
			}
			writeHeaderTo(header, os);
		}
		if (entity != null) {
			Long len = entity.getContentLength();
			writeHeaderTo(new BasicHeader(HDR_CONTENT_LENGTH, len.toString()), os);
			writeHeaderTo(entity.getContentEncoding(), os);
			writeHeaderTo(entity.getContentType(), os);
		}
		os.write(CRLF);
		if (entity != null) {
			os.write(IO.readAndClose(entity.getContent()));
		}
	}

	private void writeHeaderTo(@Nullable Header header, OutputStream os) throws IOException {
		if(header == null || header.getValue() == null) {
			return;
		}
		char[] carr = LINE_FORMATTER.formatHeader(null, header).toCharArray();
		os.write(new String(carr).getBytes(CS_ASCII));
		os.write(CRLF);
	}

	public StatusLine statusLine(int code) {
		String reason = Check.ifNull(HttpProtocol.statusToReason.get(code), "XXX");
		return statusLine(code, reason);
	}

	public StatusLine statusLine(int code, String reason) {
		return statusLine(code, reason, ProtocolVersion_1_1);
	}

	public StatusLine statusLine(int code, String reason, ProtocolVersion httpVersion) {
		Check.illegalargument.assertTrue(code > 99 && code < 600);
		return new BasicStatusLine(Check.notNull(httpVersion), code, Check.notNull(reason));
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

	public List<NameValuePair> parseEntityWithDefaultUtf8(final HttpEntity entity) throws IOException {
		List<NameValuePair> result = new LinkedList<NameValuePair>();
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
		if (contentType != null && contentType.trim().toLowerCase().startsWith(CT_WWW_FORM_URLENCODED.toLowerCase())) {
			final String content = EntityUtils.toString(entity, charset);
			URLEncodedUtils.parse(result, new Scanner(content), charset);
//			parseUrlEncodedParamList(result, content, charset);
		} else if (isMultipart(entity)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
			try {
				MimeMultipart mm = new MimeMultipart(new ByteArrayDataSource(baos.toByteArray(), CT_APPLICATION_OCTET_STREAM));
				for (int i=0; i<mm.getCount(); i++) {
					BodyPart bp = mm.getBodyPart(i);
					String name = getBodyPartName(bp);
					if (name != null) {
						String value = null;
						switch (bp.getContentType()) {
						case CT_TEXT_PLAIN:
							value = bp.getContent().toString();
							break;
						case CT_APPLICATION_OCTET_STREAM:
							byte[] bytes = IO.readAndClose(bp.getInputStream());
							value = base64.encodeClassic(bytes);
							break;
						}
						result.add(new BasicNameValuePair(name, value));
					}
				}
			} catch (MessagingException e) { // to avoid propagation of a javax exception
				throw new IOException();
			}
		}
		return result;
	}

	private void parseUrlEncodedParamList(List<NameValuePair> result, String content, String charset) {
		try {
			content = URLDecoder.decode(content, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		if (content != null && content.length() > 0) {
			char[] carr = content.toCharArray();
			int len = carr.length;
			StringBuilder name = new StringBuilder();
			StringBuilder value = new StringBuilder();
			int STATE_PARSING_KEY = 0;
			int STATE_PARSING_VAL = 1;
			int state = STATE_PARSING_KEY;
			for (int i = 0; i < len; i++) {
				char c = carr[i];
				switch (c) {
//					case '%':
//						Check.illegalargument.assertTrue(len > i + 2);
//						String sss = new StringBuilder(2)
//							.append(carr[i+1])
//							.append(carr[i+2])
//							.toString();
//						i += 2;
//						char rr = Character.forDigit(Integer.parseInt(sss, 16), 16);
//						if(state == STATE_PARSING_KEY) {
//							name.append(rr);
//						} else {
//							value.append(rr);
//						}
//						break;
				case '=':
					if (state == STATE_PARSING_KEY) {
						state = STATE_PARSING_VAL;
						Check.illegalargument.assertTrue(name.length() > 0);
					} else {
						value.append(c);
					}
					break;
				case '&':
					Check.illegalargument.assertTrue(state == STATE_PARSING_VAL);
					result.add(new BasicNameValuePair(
						name.toString(), 
						value.toString()));
					name = new StringBuilder();
					value = new StringBuilder();
					state = STATE_PARSING_KEY;
					break;
//					case '+':
//						if(state == STATE_PARSING_KEY) {
//							name.append(' ');
//						} else {
//							value.append(' ');
//						}
//						break;
				default:
					if (state == STATE_PARSING_KEY) {
						name.append(c);
					} else {
						value.append(c);
					}
					break;
				}
			}
			if (name.length() > 0) {
				result.add(new BasicNameValuePair(
						name.toString(), 
						value.toString()));
			}
		}
	}
	
	private String getBodyPartName(BodyPart bp) throws MessagingException {
		String[] d = bp.getHeader("Content-Disposition");
		if (d.length == 1) {
			Matcher matcher = MULTIPART_NAME_RE.matcher(d[0]);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
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
		resp.setEntity(new StringEntity(msg, ContentType.create(CT_TEXT_PLAIN, CS_UTF_8)));
		return resp;
	}

	public HttpResponse resp500TextPlainUtf8(Exception e) {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(500));
		resp.setEntity(new StringEntity(Exceptions.toStackTrace(e), ContentType.create(CT_TEXT_PLAIN, CS_UTF_8)));
		return resp;
	}

	public HttpResponse respInternalServerError() {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(500));
		resp.setEntity(new StringEntity(
			"Internal Server Error", ContentType.create(CT_TEXT_PLAIN, CS_UTF_8)));
		return resp;
	}

	public HttpResponse createBasicResponse(int status, String message,
			String contentType) {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(status));
		try {
			String declaredCharset = guessCharset(contentType);
			HttpEntity e = new StringEntity(message, 
				declaredCharset == null
					? ContentType.create(contentType, CS_UTF_8)
					: ContentType.create(contentType));
			resp.setEntity(e);
		} catch (UnsupportedCharsetException ex) {
			throw new IllegalArgumentException(ex);
		}
		return resp;
	}

	public HttpResponse createBasicResponse(int status, String reason) {
		BasicHttpResponse resp = new BasicHttpResponse(
				statusLine(status, reason));
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
		return createClient(maxConnPerRoute, connTimeoutMillis, soTimeoutMillis, null);
	}

	//if proxyHostAnPort value is jvm the normal jvm settings apply
	public HttpClient createClient(int maxConnPerRoute, long connTimeoutMillis, long soTimeoutMillis, @Nullable String proxyHostAnPort) {
		Registry<ConnectionSocketFactory> defaultRegistry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSocketFactory())
				.build();
			PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(defaultRegistry);
			connMgr.setDefaultMaxPerRoute(maxConnPerRoute);
			SocketConfig sCfg = SocketConfig.custom()
				.setSoTimeout((int)soTimeoutMillis)
				.setSoTimeout((int)connTimeoutMillis)
				.build();
			connMgr.setDefaultSocketConfig(sCfg);
		HttpClientBuilder hcb = HttpClientBuilder.create();
		hcb.setDefaultSocketConfig(sCfg).setConnectionManager(connMgr);
		if(proxyHostAnPort == null) {
		} else if("jvm".equalsIgnoreCase(proxyHostAnPort)) {
			SystemDefaultRoutePlanner rp = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
			hcb.setRoutePlanner(rp);
		} else {
			String[] hostAndPort = proxyHostAnPort.split(":");
			Check.illegalargument.assertTrue(hostAndPort.length < 3, "wrong hostAndPort:"+proxyHostAnPort);
			String host = hostAndPort[0];
			int port = 80;
			if(hostAndPort.length > 1) {
				port = Integer.valueOf(hostAndPort[1]);
			}
			HttpHost proxy = new HttpHost(host, port);
			hcb.setProxy(proxy);
		}
		HttpClient httpClient = hcb.build();
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
