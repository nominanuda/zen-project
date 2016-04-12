package com.nominanuda.web.http;


public enum BasicApiError implements IApiError {
	generic_badParameter,
	generic_unauthorized,
	generic_unknown;
	
	@Override
	public String param() {
		return null;
	}
	@Override
	public BasicApiError param(String param) {
		return this;
	}
}
