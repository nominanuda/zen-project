package com.nominanuda.web.mvc;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataStruct;

public interface HandlerFilter {
	/**
	 * 
	 * @param request
	 * @param cmd
	 * @param handler
	 * @return null or an object to be treated as the handler return value; in the latter case
	 * execution of the chain should be suspended
	 * @throws Exception
	 */
	@Nullable Object before(HttpRequest request, DataStruct cmd, Object handler)
			throws Exception;

	void after(HttpRequest request, DataStruct cmd, Object handler,
			Object handlerReturnValue) throws Exception;

	void afterCompletion(HttpRequest request, HttpResponse response,
			Object handler, Object handlerReturnValue, Exception ex)
			throws Exception;

}
