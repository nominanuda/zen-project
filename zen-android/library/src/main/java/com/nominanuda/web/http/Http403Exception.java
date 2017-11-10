package com.nominanuda.web.http;


public class Http403Exception extends Http4xxException {
	private static final long serialVersionUID = -6143578958339194293L;

	public Http403Exception(Exception e) {
		super(e, 403);
	}

	public Http403Exception(String msg) {
		super(msg, 403);
	}

	public Http403Exception(IApiError apiError) {
		super(apiError, 403);
	}
}
