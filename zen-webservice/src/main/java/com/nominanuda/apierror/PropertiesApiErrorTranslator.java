package com.nominanuda.apierror;

import java.util.Properties;

import com.nominanuda.web.http.IApiError;

public class PropertiesApiErrorTranslator extends ApiErrorTranslator {
	private Properties properties;
	
	@Override
	final protected String error2message(IApiError err) {
		return properties.containsKey(err.name()) ? properties.getProperty(err.name()) : super.error2message(err);
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}