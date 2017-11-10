package com.nominanuda.hyperapi;

import com.nominanuda.web.http.HttpAppException;

/**
 * Created by azum on 07.11.17.
 */

public interface IHttpAppExceptionRenderer {
	/**
	 * From response to exception
	 *
	 * @param status   Http status to translate into exception
	 * @param response To analyze for exceptions
	 */
	public void parseAndThrow(int status, Object response) throws HttpAppException;
}
