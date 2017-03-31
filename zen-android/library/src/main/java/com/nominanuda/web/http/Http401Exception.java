package com.nominanuda.web.http;




public class Http401Exception extends Http4xxException {
	private static final long serialVersionUID = -335006907602224910L;
	
	public Http401Exception(Exception e) {
		super(e);
	}

	public Http401Exception(String msg) {
		super(msg);
	}
	
	public Http401Exception(IApiError err) {
		this(serialize(err));
	}
	
	@Override
	public int getStatusCode() {
		return 401;
	}
}
