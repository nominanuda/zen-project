package com.nominanuda.web.http;


public enum BasicApiError implements IApiError {
	generic_badParameter,
	generic_unauthorized,
	generic_unknown;
	
	private String p;
	@Override
	public String param() {
		return p;
	}
	@Override
	public BasicApiError param(String param) {
		this.p = param;
		return this;
	}
}
