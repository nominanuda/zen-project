package com.nominanuda.hyperapi;

import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http401Exception;
import com.nominanuda.web.http.Http403Exception;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http4xxException;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.Http5xxException;
import com.nominanuda.web.http.HttpAppException;

/**
 * Created by azum on 09.11.17.
 */

public class HttpExceptionRenderer implements IHttpAppExceptionRenderer {

	@Override
	public void parseAndThrow(int status, Object response) throws HttpAppException {
		if (status >= 400) {
			String message = response != null ? response.toString() : "";
			if (status < 500) {
				switch (status) {
				case 400:
					throw new Http400Exception(message);
				case 401:
					throw new Http401Exception(message);
				case 403:
					throw new Http403Exception(message);
				case 404:
					throw new Http404Exception(message);
				default:
					throw new Http4xxException(message, status);
				}
			}
			throw status == 500
					? new Http500Exception(message)
					: new Http5xxException(message, status);
		}
	}
}
