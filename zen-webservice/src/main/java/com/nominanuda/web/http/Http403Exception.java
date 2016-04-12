package com.nominanuda.web.http;




public class Http403Exception extends Http4xxException {
	private static final long serialVersionUID = -6143578958339194293L;

	public Http403Exception(Exception e) {
		super(e);
	}

	public Http403Exception(String msg) {
		super(msg);
	}
	
	public Http403Exception(IApiError err) {
		this(serialize(err));
	}
	
	@Override
	public int getStatusCode() {
		return 403;
	}
}
