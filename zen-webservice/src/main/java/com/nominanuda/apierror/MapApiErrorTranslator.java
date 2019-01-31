package com.nominanuda.apierror;

import java.util.Map;

import com.nominanuda.web.http.IApiError;

public class MapApiErrorTranslator extends ApiErrorTranslator {
	private Map<String, String> messages;
	
	@Override
	final protected String error2message(IApiError err) {
		return messages.containsKey(err.name()) ? messages.get(err.name()) : super.error2message(err);
	}
	
	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}
}