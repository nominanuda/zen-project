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

import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.nominanuda.web.http.HttpProtocol;

public class StaticViewResolver implements ViewResolver {
	protected String contentType = HttpProtocol.CT_APPLICATION_OCTET_STREAM;
	protected boolean cache = false;

	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		URL url = resolve(viewName, locale);
		if(url == null) {
			return null;
		} else {
			if(cache) {
				View cv = findCachedView(url);
				if(cv != null) {
					return cv;
				}
			}
			final byte[] barr = IO.readAndClose(url.openStream());
			View v = new View() {
				public void render(Map<String, ?> model, HttpServletRequest request,
						HttpServletResponse response) throws Exception {
					response.setContentLength(barr.length);
					response.setContentType(contentType);
					OutputStream os = response.getOutputStream();
					os.write(barr);
					os.flush();
				}
				public String getContentType() {
					return contentType;
				}
			};
			if(cache) {
				storeCachedView(url, v);
			}
			return v;
		}
	}

	private Map<URL, View> viewCache = new HashMap<URL, View>();
	protected void storeCachedView(URL url, View v) {
		viewCache.put(url, v);
	}

	protected View findCachedView(URL url) {
		return viewCache.get(url);
	}

	protected String suffix = "";
	protected URL resolve(String viewName, Locale locale) throws IOException {
		if(! viewName.endsWith(suffix)) {
			return null;
		}
		return new URL(viewName);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
