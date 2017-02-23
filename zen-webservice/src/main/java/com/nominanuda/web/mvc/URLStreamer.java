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
package com.nominanuda.web.mvc;

import static com.nominanuda.zen.oio.OioUtils.IO;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;

import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.http.MimeHelper;

public abstract class URLStreamer implements WebService, HttpProtocol {
	private static final HttpCoreHelper httpCoreHelper = new HttpCoreHelper();
	private static final MimeHelper mimeHelper = new MimeHelper();
	private String defaultContentType = CT_APPLICATION_OCTET_STREAM;

	public HttpResponse handle(HttpRequest request) throws IOException {
		URL url = getURL(request);
		URLConnection conn = url.openConnection();
		conn.connect();
		int len = conn.getContentLength();
		InputStream is = conn.getInputStream();
		String ce = conn.getContentEncoding();
		String ct = determineContentType(url, conn);
		if(len < 0) {
			byte[] content = IO.readAndClose(is);
			is = new ByteArrayInputStream(content);
			len = content.length;	
		}
		StatusLine statusline = httpCoreHelper.statusLine(SC_OK);
		HttpResponse resp = new BasicHttpResponse(statusline);
		resp.setEntity(new InputStreamEntity(is, len));
		httpCoreHelper.setContentType(resp, ct);
		httpCoreHelper.setContentLength(resp, len);//TODO not needed ??
		if(ce != null) {
			httpCoreHelper.setContentEncoding(resp, ce);
		}
		return resp;
	}

	private String determineContentType(URL url, URLConnection conn) {
		String ct = conn.getContentType();
		if(ct == null || "content/unknown".equals(ct)) {
			ct = mimeHelper.guessContentTypeFromPath(url.toString());
			//TODO add default charset if contenttype is textual
		}
		if(ct == null) {
			ct = defaultContentType;
		}
		return ct;
	}

	protected abstract URL getURL(HttpRequest request) throws IllegalArgumentException;

	public void setDefaultContentType(String defaultContentType) {
		this.defaultContentType = defaultContentType;
	}

}
