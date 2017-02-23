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
package com.nominanuda.hyperapi;

import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.zen.common.Tuple2;

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
