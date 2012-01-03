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

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Collections;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.web.http.HttpCoreHelper;

public class FrontController implements WebService {
	private static final HttpCoreHelper httpCore = new HttpCoreHelper();
	private List<HandlerMatcher> matchers;
	private List<? extends HandlerAdapter> adapters = Collections.fixedList(new WebServiceAdapter());

	@Override
	public HttpResponse handle(HttpRequest request) throws Exception {
		try {
			Tuple2<Object, DataStruct<?>> handlerAndContext = getHandlerAndContext(request);
			if(handlerAndContext == null) {
				return httpCore.resp404TextPlainUtf8("not found");
			}
			HandlerAdapter adapter = getAdapter(handlerAndContext.get0());
			if(adapter == null) {
				return httpCore.respInternalServerError();
			}
			Object result = adapter.invoke(handlerAndContext.get0(), 
					request, handlerAndContext.get1());
			if(result instanceof HttpResponse) {
				return(HttpResponse)result;
			} else if(result instanceof HttpEntity) {
				HttpResponse resp = httpCore.createBasicResponse(200);
				resp.setEntity((HttpEntity)result);
				return resp;
			} else if(result instanceof ViewSpec) {
				throw new UnsupportedOperationException();
			} else {
				throw new IllegalStateException();
			}
		} catch(Exception e) {
			return httpCore.resp500TextPlainUtf8(e);
		}
	}

	protected @Nullable HandlerAdapter getAdapter(Object handler) {
		List<HandlerAdapter> adapters = getAdapters();
		for(HandlerAdapter a : adapters) {
			if(a.supports(handler)) {
				return a;
			}
		}
		return null;
	}

	private @Nullable Tuple2<Object, DataStruct<?>> getHandlerAndContext(HttpRequest request) {
		List<HandlerMatcher> matchers = getMatchers();
		for(HandlerMatcher m : matchers) {
			Tuple2<Object, DataStruct<?>> candidate = m.match(request);
			if(candidate != null) {
				return candidate;
			}
		}
		return null;
	}

	protected List<HandlerMatcher> getMatchers() {
		return matchers;
	}

	public void setMatchers(List<HandlerMatcher> matchers) {
		this.matchers = matchers;
	}

	@SuppressWarnings("unchecked")
	protected List<HandlerAdapter> getAdapters() {
		return (List<HandlerAdapter>)adapters;
	}

	public void setAdapters(List<HandlerAdapter> adapters) {
		this.adapters = adapters;
	}
}
