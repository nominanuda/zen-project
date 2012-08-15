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
package com.nominanuda.web.mvc;

import org.apache.http.HttpRequest;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Tuple2;

public interface HandlerMatcher {
	/**
	 * 
	 * @param request
	 * @return the handler object or {@link HandlerAndFilters} plus the bound data during request matching
	 */
	@Nullable Tuple2<Object, DataStruct> match(HttpRequest request);
}
