package com.nominanuda.apierror;

import com.nominanuda.web.http.IApiError;



public enum ApiError implements IApiError {
	// TODO cumulate here common api errors
	;
	
	
	
	//==================================//
	
	private String p;
	@Override
	public String param() {
		return p;
	}
	@Override
	public ApiError param(String param) {
		this.p = param;
		return this;
	}
}
