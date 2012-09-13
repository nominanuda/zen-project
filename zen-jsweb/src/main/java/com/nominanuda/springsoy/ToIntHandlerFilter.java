package com.nominanuda.springsoy;

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
import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.NoException;
import com.nominanuda.lang.SafeConvertor;
import com.nominanuda.web.mvc.HandlerFilter;

public class ToIntHandlerFilter implements HandlerFilter {
	public Object before(HttpRequest request, DataStruct cmd, Object handler)
			throws Exception {
		return null;
	}

	public void after(HttpRequest request, DataStruct cmd, Object handler,
			Object handlerReturnValue) throws Exception {
		if(handlerReturnValue != null  && handlerReturnValue instanceof DataStruct) {
			STRUCT.convertLeaves((DataStruct)handlerReturnValue, new SafeConvertor<Object,Object>() {
				public Object apply(Object x) throws NoException {
					return ((Long)x).intValue();
				}
				public boolean canConvert(Object o) {
					return o != null && o instanceof Long;
				}
			});
		}
	}

	public void afterCompletion(HttpRequest request, HttpResponse response,
			Object handler, Object handlerReturnValue, Exception ex)
			throws Exception {
	}

}
