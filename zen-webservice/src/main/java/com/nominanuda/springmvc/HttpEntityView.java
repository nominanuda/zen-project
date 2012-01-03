package com.nominanuda.springmvc;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.springframework.web.servlet.View;

import com.nominanuda.io.IOHelper;
import com.nominanuda.web.http.HttpProtocol;

public class HttpEntityView implements View, HttpProtocol {
	private static final IOHelper io = new IOHelper();
	private final HttpEntity entity;
	public HttpEntityView(HttpEntity entity) {
		this.entity = entity;
	}

	public String getContentType() {
		Header ct = entity.getContentType();
		return ct == null ? CT_APPLICATION_OCTET_STREAM : ct.getValue();
	}

	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType(getContentType());
		InputStream is = entity.getContent();
		byte[] b = io.readAndClose(is);
		response.setContentLength(b.length);
		response.getOutputStream().write(b);
	}

}
