package com.nominanuda.hyperapi;

import com.nominanuda.lang.Tuple2;
import com.nominanuda.web.http.HttpAppException;

public interface IHttpAppExceptionRenderer {
	/**
	 * From exception to response
	 * @param e Exception to render
	 * @return the http status (Integer) and the rendered error (Object)
	 */
	public Tuple2<Integer, Object> statusAndRender(HttpAppException e, Class<?> returnType);
	
	/**
	 * From response to exception
	 * @param status Http status to translate into exception
	 * @param response To analyze for exceptions
	 */
	public void parseAndThrow(int status, Object response) throws HttpAppException;
}
