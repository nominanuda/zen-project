/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.webapp;

import static com.nominanuda.zen.io.Uris.URIS;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.springmvc.HttpContext;
import com.nominanuda.springmvc.Sitemap;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory;

public class Helper {
	private final Sitemap sitemap;
	private final HttpContext httpContext = HttpContext.getInstance();
	private final PublicSuffixList publicSuffixList = new PublicSuffixListFactory().build();
	private final Logger log = LoggerFactory.getLogger("webappConsole");
	private String servletPath;
	
	public Helper(Sitemap sitemap) {
		this.sitemap = sitemap;
	}
	
	
	public void logDebug(String msg) {
		log.debug(msg);
	}
	public void logError(String msg) {
		log.error(msg);
	}
	public void logInfo(String msg) {
		log.info(msg);
	}
	public void logWarn(String msg) {
		log.warn(msg);
	}
	
	
	public String getClientIp() {
		return httpContext.getClientIp();
	}
	
	public String getServletPath() {
		if (servletPath == null) {
			String ctxPath = httpContext.getServletPath();
			int i = ctxPath.indexOf("://") + 1; // starting from "//" included
			return ctxPath.substring(i);
		}
		return servletPath;
	}
	public void setServletPath(String path) {
		servletPath = Check.ifNullOrBlank(path, null);
	}

	public String absUrl(String patternId, Obj patternParams) {
		String url = sitemap.getUrl(patternId, patternParams);
		return (null != url ? URIS.pathJoin(getServletPath(), url) : null);
	}
	public String absUrl(String path) {
		return URIS.pathJoin(getServletPath(), path);
	}
	
	
	public String getCookie(String name) {
		return httpContext.getCookie(name);
	}
	public void setCookie(String name, String value) {
		httpContext.setCookie(name, value);
	}
	public void setCookieFor(String name, String value, long duration) {
		httpContext.setCookieFor(name, value, duration);
	}
	public void setCookieUntil(String name, String value, long datetime) {
		httpContext.setCookieUntil(name, value, datetime);
	}
	public void resetCookie(String name) {
		httpContext.resetCookie(name);
	}
	
	
	public @Nullable String getRequestProtocol() {
		try {
			return new URL(Check.ifNull(servletPath, httpContext.getServletPath())).getProtocol();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public @Nullable String getRequestHost() {
		try {
			return new URL(Check.ifNull(servletPath, httpContext.getServletPath())).getHost();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public @Nullable Integer getRequestPort() {
		try {
			return new URL(Check.ifNull(servletPath, httpContext.getServletPath())).getPort();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public @Nullable String getRequestTopLevelDomain() {
		@Nullable String host = getRequestHost();
		return host != null ? publicSuffixList.getPublicSuffix(host) : null;
	}
}
