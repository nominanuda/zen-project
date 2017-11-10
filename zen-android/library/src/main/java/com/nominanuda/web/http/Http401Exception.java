package com.nominanuda.web.http;


public class Http401Exception extends Http4xxException {
	private static final long serialVersionUID = -335006907602224910L;

	public Http401Exception(Exception e) {
		super(e, 401);
	}

	public Http401Exception(String msg) {
		super(msg, 401);
	}

	public Http401Exception(IApiError apiError) {
		super(apiError, 401);
	}
}
