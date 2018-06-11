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
package com.nominanuda.springmvc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.zen.common.Check;


public class HttpContext {
	private final static String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
	private final static String HEADER_X_FORWARDED_URL = "X-Forwarded-URL";
	private final static String COOKIE_DOMAIN = "*"; // TODO settable via spring?

	
	private final ThreadLocal<Boolean> initialized = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return false;
		};
	};
	
	private final ThreadLocal<Map<String, Cookie>> requestCookies = new ThreadLocal<Map<String, Cookie>>() {
		protected Map<String, Cookie> initialValue() {
			return new HashMap<String, Cookie>();
		}
	};
	private final ThreadLocal<Map<String, Cookie>> responseCookies = new ThreadLocal<Map<String, Cookie>>() {
		protected Map<String, Cookie> initialValue() {
			return new HashMap<String, Cookie>();
		}
	};
	
	private final ThreadLocal<String> clientIp = new ThreadLocal<String>();
	private final ThreadLocal<String> contextUrl = new ThreadLocal<String>();
	private final ThreadLocal<String> contextPath = new ThreadLocal<String>();
	private final ThreadLocal<String> contextRequest = new ThreadLocal<String>();
	
	private final ThreadLocal<Long> contextTsStart = new ThreadLocal<Long>();
	private final ThreadLocal<Long> contextTsCurrent = new ThreadLocal<Long>();
	private final ThreadLocal<Long> contextMsProcess = new ThreadLocal<Long>();
	private final ThreadLocal<Long> contextMsNetwork = new ThreadLocal<Long>();

	private final Logger log = LoggerFactory.getLogger(HttpContext.class);
	private final CookieSpec cookieSpec = new DefaultCookieSpec();
	private Set<String> forwardedCookies = new HashSet<>();
	private boolean verbose = false;


	private static final HttpContext THIS = new HttpContext();
	public static synchronized HttpContext getInstance() {
		return THIS;
	}

	
	/* request - client cookies -> thread local */
	
	public void init(HttpServletRequest request) {
		resetTimers();
		
		if (request != null) {
			String p = request.getPathInfo();
			final int pathLength = (p == null ? 0 : p.length());
			
			final StringBuffer urlSb = request.getRequestURL();
			String simpleUrl = urlSb.toString(); // without qs
			
			String qs = request.getQueryString();
			if (qs != null) urlSb.append("?").append(qs);
			final String fullUrl = urlSb.toString();
			contextUrl.set(fullUrl);
		
			requestCookies.remove();
			responseCookies.remove();
			
			StringBuilder cookiesDump = new StringBuilder();
			Map<String, Cookie> cookiez = requestCookies.get();
			for (javax.servlet.http.Cookie c : Check.ifNull(request.getCookies(), new javax.servlet.http.Cookie[0])) {
				cookiez.put(c.getName(), new BasicClientCookie(c.getName(), c.getValue()));
				cookiesDump.append(c.toString()).append(";");
			}
	
			String xForwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);
			clientIp.set(xForwardedFor != null ? xForwardedFor : request.getRemoteAddr());
			
			String xForwardedUrl = request.getHeader(HEADER_X_FORWARDED_URL);
			if (xForwardedUrl != null) {
				int i = xForwardedUrl.lastIndexOf("?");
				simpleUrl = (i == -1 ? xForwardedUrl : xForwardedUrl.substring(0, i));
			}
			
			try {
				simpleUrl = URLDecoder.decode(simpleUrl, "UTF-8"); // convert "%xx" stuff before working with lengths
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString());
			}
			contextPath.set(simpleUrl.substring(0, simpleUrl.length() - pathLength));

			if (verbose) log.info(">>> Time 0 [" + fullUrl + "]. cookies from client: " + cookiesDump.toString());
		}
		
		initialized.set(true);
	}
	
	
	/* request - thread local cookies -> other webapp */
	
	public void writeTo(HttpRequest request) {
		if (!initialized.get()) { // to avoid npe
			init(null);
		}
		
		long time = System.currentTimeMillis();
		long processTime = time - contextTsCurrent.get();
		contextMsProcess.set(contextMsProcess.get() + processTime);
		contextTsCurrent.set(time);
		
		final String uri = request.getRequestLine().getUri();
		contextRequest.set(uri);
		
		List<Cookie> cookiez = new ArrayList<>();
		StringBuilder cookiesDump = new StringBuilder();
		for (String name : forwardedCookies) {
			Cookie c = requestCookies.get().get(name);
			if (c != null) {
				cookiez.add(c);
				cookiesDump.append(c.toString()).append(";");
			}
		}
		if (!cookiez.isEmpty()) { // ...or formatCookies() throws error
			for (Header h : cookieSpec.formatCookies(cookiez)) {
				request.addHeader(h);
			}
		}

		request.setHeader(HEADER_X_FORWARDED_FOR, clientIp.get());

		if (verbose) log.info("    Time " + String.valueOf(time - contextTsStart.get()) + " (+" + processTime + "p) >>> [" + contextUrl.get() + "] >>> " + uri + ". thread local cookies " + cookiesDump.toString());
	}
	
	
	/* response - other webapp set-cookies -> thread local */
	
	public void update(HttpResponse response) {
		long time = System.currentTimeMillis();
		long networkTime = time - contextTsCurrent.get();
		contextMsNetwork.set(contextMsNetwork.get() + networkTime);
		contextTsCurrent.set(time);
		
		StringBuilder cookiesDump = new StringBuilder();
		for (Header h : response.getAllHeaders()) {
			if ("Set-Cookie".equalsIgnoreCase(h.getName())) {
				try {
					Map<String, Cookie> cookiez = getServerCookiesFrom(h);
					requestCookies.get().putAll(cookiez);
					responseCookies.get().putAll(cookiez);
					cookiesDump.append(h.toString()).append(";");
				} catch (MalformedCookieException e) {
					log.error("discarding malformed cookie", e);
				}
			}
		}

		if (verbose) log.info("    Time " + String.valueOf(time - contextTsStart.get()) + " (+" + networkTime + "n) <<< [" + contextUrl.get() + "] <<< " + contextRequest.get() + ". Set-Cookie to thread local: " + cookiesDump.toString());
	}
	
	
	/* response - thread local set-cookies -> client */
	
	public void writeTo(HttpServletResponse response) {
		long time = System.currentTimeMillis();
		long processTime = time - contextTsCurrent.get();
		long processTimeTotal = contextMsProcess.get() + processTime;
		long networkTimeTotal = contextMsNetwork.get();
		
		StringBuilder cookiesDump = new StringBuilder();
		for (Entry<String, Cookie> entry : responseCookies.get().entrySet()) {
			javax.servlet.http.Cookie c = buildServletCookie(entry.getKey(), entry.getValue());
			response.addCookie(c);
			cookiesDump.append(c.toString()).append(";");
		}
		
		if (verbose) log.info("<<< Time total " + String.valueOf(time - contextTsStart.get()) + " (+" + processTime + "p " + processTimeTotal + "p " + networkTimeTotal + "n)" + " [" + contextUrl.get() + "]" + ". Set-Cookie to client: " + cookiesDump.toString());
	}

	
	
	/* access thread locals */
	
	public String getCookie(String name) {
		Cookie c = requestCookies.get().get(name);
		return c == null ? null : c.getValue();
	}

	public void setCookieUntil(String name, String value, Long datetime) {
		BasicClientCookie c = new BasicClientCookie(name, value);
		c.setExpiryDate(datetime != null ? new Date(datetime) : null);
		requestCookies.get().put(name, c);
		responseCookies.get().put(name, c);
	}
	public void setCookieFor(String name, String value, long duration) {
		Check.illegalargument.assertFalse(duration == 0, "use setCookie(...) instead of duration == 0");
		Check.illegalargument.assertFalse(duration < 0, "use resetCookie(...) instead of duration < 0");
		setCookieUntil(name, value, System.currentTimeMillis() + duration);
	}
	public void setCookie(String name, String value) {
		setCookieUntil(name, value, null);
	}

	public void resetCookie(String name) {
		if (requestCookies.get().remove(name) != null) {
			responseCookies.get().put(name, null);
		}
	}

	public String getClientIp() {
		return clientIp.get();
	}
	
	public String getServletPath() {
		return contextPath.get();
	}
	
	
	
	/* helpers */
	
	private Map<String, Cookie> getServerCookiesFrom(Header h) throws MalformedCookieException {
		CookieOrigin origin = new CookieOrigin(COOKIE_DOMAIN, 80, "/", true);
		Map<String, Cookie> m = new HashMap<String, Cookie>();
		for (Cookie c : cookieSpec.parse(h, origin)) {
			m.put(c.getName(), c);
		}
		return m;
	}

	private javax.servlet.http.Cookie buildServletCookie(String name, Cookie cookie) {
		javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(name, "");
		if (cookie == null) {
			c.setMaxAge(0); // delete
		} else {
			c.setValue(cookie.getValue());
			c.setComment(cookie.getComment());
			c.setVersion(cookie.getVersion());
			c.setSecure(cookie.isSecure());
			// c.setDomain(""/*cookie.getDomain()*/);
			Date d = cookie.getExpiryDate();
			c.setMaxAge(d != null ? (int)((d.getTime() - System.currentTimeMillis()) / 1000) : -1);
		}
		c.setPath("/"/* cookie.getPath() */);
		return c;
	}
	
	private void resetTimers() {
		long time = System.currentTimeMillis();
		contextTsStart.set(time);
		contextTsCurrent.set(time);
		contextMsProcess.set(0l);
		contextMsNetwork.set(0l);
	}
	
	
	
	/* setters */
	
	public void setForwardedCookies(Set<String> forwardedCookies) {
		this.forwardedCookies = forwardedCookies;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}
