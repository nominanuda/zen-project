package com.nominanuda.springmvc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.nominanuda.io.IOHelper;
import com.nominanuda.web.http.HttpProtocol;

public class StaticViewResolver implements ViewResolver {
	protected static final IOHelper io = new IOHelper();
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
			final byte[] barr = io.readAndClose(url.openStream());
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
