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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.HttpCookie;
import java.util.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.io.IOHelper;
import com.nominanuda.lang.Check;

@ThreadSafe
public class ServletHelper {
	private final static IOHelper ioHelper = new IOHelper();
	/**
	 * @param request
	 * @param stripContextPath 
	 * @return the raw uri as in the request line (verbatim unescaped)
	 */
	public String getRequestLineURI(HttpServletRequest request, boolean stripContextPath) {
		String query = request.getQueryString();
		String path = request.getRequestURI();
		if(stripContextPath) {
			path = path.substring(Check.ifNull(request.getContextPath(), "").length());
		}
		return query == null ? path : path + "?" + query;
	}

	/**
	 * @param servletRequest
	 * @return an InputStream that contains at least one byte or null if underlying stream is null or empty
	 * @throws IOException if thrown while accessing the underlying ServletInputStream
	 */
	public @Nullable InputStream getServletRequestBody(HttpServletRequest servletRequest) throws IOException {
		ServletInputStream sis = servletRequest.getInputStream();
		if(servletRequest.getContentLength() > 0) {
			return sis;
		} else {
			return null;
		}
	}

	public @Nullable String getCharacterEncoding(HttpServletRequest servletRequest) {
		return servletRequest.getCharacterEncoding();
	}

	public @Nullable String getContentEncoding(HttpServletRequest servletRequest) {
		return servletRequest.getHeader("Content-Encoding");
	}

	public void copyResponse(HttpResponse response, HttpServletResponse servletResponse)
			throws IOException {
		servletResponse.setStatus(response.getStatusLine().getStatusCode());
		for(Header h : response.getAllHeaders()) {
			if("Set-Cookie".equals(h.getName()) || "Set-Cookie2".equals(h.getName())) {
				for(HttpCookie c : HttpCookie.parse(h.getValue())) {
					servletResponse.addCookie(servletCookie(c));
				}
			}
			else {
				servletResponse.setHeader(h.getName(), h.getValue());
			}
		}
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			Header ct = entity.getContentType();
			if(ct != null) {
				servletResponse.setContentType(ct.getValue());
			}
			Header ce = entity.getContentEncoding();
			if(ce != null) {
				servletResponse.setHeader(ce.getName(), ce.getValue());
			}
			long len = entity.getContentLength();
			if(len >= 0) {
				servletResponse.setContentLength((int)len);
			}
			ioHelper.pipe(entity.getContent(), servletResponse.getOutputStream(), true, false);
		}
	}

	public Cookie servletCookie(HttpCookie c) {
		Cookie _c = new Cookie(c.getName(), c.getValue());
		if(c.getComment() != null) {
			_c.setComment(c.getComment());
		}
		if(c.getDomain() != null) {
			_c.setDomain(c.getDomain());
		}
		if(c.getPath() != null) {
			_c.setPath(c.getPath());
		}
		_c.setSecure(c.getSecure());
		_c.setVersion(c.getVersion());
		_c.setHttpOnly(c.getDiscard());
		_c.setMaxAge((int)c.getMaxAge());
		return _c;
	}

	public HttpRequest copyRequest(HttpServletRequest servletRequest, boolean stripContextPath) throws IOException {
		final InputStream is = getServletRequestBody(servletRequest);
		String method = servletRequest.getMethod();
		String uri = getRequestLineURI(servletRequest, stripContextPath);
		String ct = servletRequest.getContentType();
		@SuppressWarnings("unused")
		String charenc = getCharacterEncoding(servletRequest);
		String cenc = getContentEncoding(servletRequest);
		long contentLength = servletRequest.getContentLength();
		HttpRequest req;
		if(is == null) {
			req  = new BasicHttpRequest(method, uri);
		} else {
			req = new BasicHttpEntityEnclosingRequest(method, uri);
			HttpEntity entity = buildEntity(servletRequest, is, contentLength, ct, cenc);
			if(entity != null) {
				((BasicHttpEntityEnclosingRequest)req).setEntity(entity);
			}
		}
		Enumeration<?> names = servletRequest.getHeaderNames();
		while(names.hasMoreElements()) {
			String name = (String)names.nextElement();
			Enumeration<?> vals = servletRequest.getHeaders(name);
			while(vals.hasMoreElements()) {
				String value = (String)vals.nextElement();
				req.addHeader(name, value);
			}
		}
		return req;
	}

	@SuppressWarnings("unchecked")
	private HttpEntity buildEntity(HttpServletRequest servletRequest, final InputStream is, long contentLength, String ct, String cenc) throws IOException {
		if(ServletFileUpload.isMultipartContent(servletRequest)) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items;
			try {
				items = upload.parseRequest(new HttpServletRequestWrapper(
						servletRequest) {
					public ServletInputStream getInputStream()
							throws IOException {
						return new ServletInputStream() {
							public int read() throws IOException {
								return is.read();
							}
							public int read(byte[] arg0) throws IOException {
								return is.read(arg0);
							}
							public int read(byte[] b, int off, int len)
									throws IOException {
								return is.read(b, off, len);
							}
						};
					}
				});
			} catch (FileUploadException e) {
				throw new IOException(e);
			}
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for(FileItem i : items) {
				multipartEntity.addPart(i.getFieldName(), new InputStreamBody(i.getInputStream(), i.getName()));
			}
			return multipartEntity;
		} else {
			InputStreamEntity entity = new InputStreamEntity(is, contentLength);
			entity.setContentType(ct);
			if(cenc != null) {
				entity.setContentEncoding(cenc);
			}
			return entity;
		}
	}

	public HttpRequest getOrCreateRequest(HttpServletRequest servletRequest, boolean stripContextPath) throws IOException {
		HttpRequest req = (HttpRequest)servletRequest.getAttribute("__HttpRequest__");
		if(req == null) {
			req = copyRequest(servletRequest, stripContextPath);
			servletRequest.setAttribute("__HttpRequest__", req);
		}
		return req;
	}
	public void storeCommand(HttpServletRequest servletRequest, DataStruct command) throws IOException {
		servletRequest.setAttribute("__command__", command);
	}
	public @Nullable DataStruct getCommand(HttpServletRequest servletRequest) throws IOException {
		return (DataStruct)servletRequest.getAttribute("__command__");
	}

	public void storeHandlerOutput(HttpServletRequest servletRequest, Object handlerOutput) {
		servletRequest.setAttribute("__handlerOutput__", handlerOutput);
	}
	public @Nullable Object getHandlerOutput(HttpServletRequest servletRequest) throws IOException {
		return servletRequest.getAttribute("__handlerOutput__");
	}

	public void storeResponse(HttpServletRequest servletRequest, HttpResponse response) {
		servletRequest.setAttribute("__HttpResponse__", response);
	}
	public @Nullable HttpResponse getResponse(HttpServletRequest servletRequest) throws IOException {
		return (HttpResponse)servletRequest.getAttribute("__HttpResponse__");
	}
}
