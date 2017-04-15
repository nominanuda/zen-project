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

import static com.nominanuda.zen.common.Check.ifNull;
import static com.nominanuda.zen.common.Str.STR;

import java.util.List;

public abstract class HttpAppException extends RuntimeException {
	private static final long serialVersionUID = 7813677042673120866L;

	public HttpAppException(Exception e) {
		super(e);
	}

	public HttpAppException(String msg) {
		super(msg);
	}
	
	public int getStatusCode() {
		return 0;
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
	
	
	/* generic situations */
	
	public static void badParamExAssertTrue(boolean cond, IApiError msg) throws Http400Exception {
		if (!cond) {
			throw new Http400Exception(msg != null ? msg : BasicApiError.generic_badParameter);
		}
	}
	public static void badParamExAssertTrue(boolean cond) throws Http400Exception {
		authExAssertTrue(cond, null);
	}
	
	public static void authExAssertTrue(boolean cond, IApiError msg) throws Http401Exception {
		if (!cond) {
			throw new Http401Exception(msg != null ? msg : BasicApiError.generic_unauthorized);
		}
	}
	public static void authExAssertTrue(boolean cond) throws Http401Exception {
		authExAssertTrue(cond, null);
	}
	
	
	/* IApiError <-> String */
	
	protected static String serialize(IApiError err) {
		return STR.joinArgs("|", err.name(), ifNull(err.param(), ""));
	}
	
	public static <E extends Enum<E> & IApiError> IApiError deserialize(String msg, Class<E> apiErrorEnum) {
		List<String> parts = STR.splitAndTrim(msg, "\\|");
		IApiError err = Enum.valueOf(apiErrorEnum, parts.get(0));
		return err.param(parts.size() > 1 ? parts.get(1) : null);
	}
}
