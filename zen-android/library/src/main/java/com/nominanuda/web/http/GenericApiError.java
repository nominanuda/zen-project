package com.nominanuda.web.http;


public class BasicApiError implements IApiError {
	private final static String NAME = "generic_error";
	private String p, m = "generic error";

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public String param() {
		return p;
	}

	@Override
	public BasicApiError param(String param) {
		this.p = param;
		return this;
	}

	public BasicApiError message(String message) {
		if (message != null) m = message;
		return this;
	}

	@Override
	public String toString() {
		return m;
	}
}
