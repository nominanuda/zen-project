package com.nominanuda.web.http;


public class GenericApiError implements IApiError {
	private final static String NAME = "generic_error";
	private final String m;
	private String p;


	public GenericApiError(String message) {
		m = message;
	}

	public GenericApiError() {
		this("generic error");
	}


	@Override
	public String name() {
		return NAME;
	}

	@Override
	public String param() {
		return p;
	}

	@Override
	public GenericApiError param(String param) {
		this.p = param;
		return this;
	}

	@Override
	public String toString() {
		return m;
	}
}
