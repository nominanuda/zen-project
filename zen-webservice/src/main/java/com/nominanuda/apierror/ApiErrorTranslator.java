package com.nominanuda.apierror;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.web.http.GenericApiError;
import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.web.http.IApiError;
import com.nominanuda.zen.common.Str;



public class ApiErrorTranslator {
	private Class<? extends Enum<? extends IApiError>> apiErrorClazz;
	
	public final IApiError deserialize(String serialized, String fallbackMsg) {
		try {
			return HttpAppException.deserialize(serialized, (Class) apiErrorClazz); // TODO avoid cast (how?)
		} catch (IllegalArgumentException | NullPointerException e0) {
			try {
				return HttpAppException.deserialize(serialized, ApiError.class);
			} catch (IllegalArgumentException | NullPointerException e1) {
				return new GenericApiError(fallbackMsg);
			}
		}
	}
	
	public final IApiError extract(Throwable t) {
		IApiError apiError = t instanceof HttpAppException
				? ((HttpAppException) t).getApiError()
				: null;
		return apiError != null ? apiError : new GenericApiError(t.getMessage());
	}
	
	public final IApiError extract(Scriptable obj) { // for rhino
		Object jex = obj.get("javaException", obj);
		if (jex instanceof NativeJavaObject) {
			return extract((Throwable)((NativeJavaObject)jex).unwrap());
		}
		return new GenericApiError(obj.toString());
	}
	
	public final String translate(IApiError error) {
		return error2message(error);
	}
	public final String translate(Throwable t) {
		return translate(extract(t));
	}
	public final String translate(Scriptable obj) { // for rhino
		return translate(extract(obj));
	}
	

	/**
	 * Override this method to change render strategy (from enum to message)
	 * @param err
	 * @return
	 */
	protected String error2message(IApiError err) {
		if (err != null) {
			StringBuilder sb = new StringBuilder(err.toString());
			if (Str.STR.notNullOrEmpty(err.param())) {
				sb.append(" (").append(err.param()).append(")");
			}
			return sb.toString();
		}
		return new GenericApiError().toString();
	}
	
	
	/* setters */
	
	public <E extends Enum<E> & IApiError> void setApiErrorEnum(Class<E> apiErrorClazz) {
		this.apiErrorClazz = apiErrorClazz;
	}
}
