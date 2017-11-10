/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.web.http;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Util;

import static com.nominanuda.zen.common.Str.STR;

public abstract class HttpAppException extends RuntimeException {
	private static final long serialVersionUID = 7813677042673120866L;
	private final IApiError apiError;
	private final int status;

	protected HttpAppException(Exception e, int status) {
		super(e);
		apiError = null;
		this.status = status;
	}

	protected HttpAppException(String msg, int status) {
		super(msg);
		apiError = null;
		this.status = status;
	}

	protected HttpAppException(IApiError apiError, int status) {
		super(serialize(apiError));
		this.apiError = apiError;
		this.status = status;
	}


	/* public stuff */

	public HttpAppException(Exception e) {
		this(e, 0);
	}

	public HttpAppException(String msg) {
		this(msg, 0);
	}

	public HttpAppException(IApiError apiError) {
		this(apiError, 0);
	}

	public IApiError getApiError() {
		return apiError;
	}

	public int getStatusCode() {
		return status;
	}

	public static HttpAppException from(Exception e) {
		if (e instanceof HttpAppException) {
			return (HttpAppException) e;
		} else if (e instanceof IllegalArgumentException) {
			return new Http400Exception(e);
		} else if (e instanceof NullPointerException) {
			return new Http500Exception(e);
		}
		return new Http500Exception(e);
	}
	
	
	/* IApiError <-> String */

	private static String serialize(IApiError err) {
		return STR.joinArgs("|", err.name(), Check.ifNull(err.param(), ""));
	}

	public static <E extends Enum<E> & IApiError> IApiError deserialize(String msg, Class<E> apiErrorEnum)
			throws IllegalArgumentException, NullPointerException {
		String param = null;
		int index = msg.indexOf('|');
		if (index > -1) {
			param = Util.notEmptyElse(msg.substring(index + 1), null);
			msg = msg.substring(0, index);
		}
		return Enum.valueOf(apiErrorEnum, msg).param(param);
	}
}
