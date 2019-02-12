/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.apierror;

import java.util.Map;

import com.nominanuda.web.http.IApiError;

public class MapApiErrorTranslator extends ApiErrorTranslator {
	private Map<String, String> messages;
	
	@Override
	final protected String error2message(IApiError err) {
		return messages.containsKey(err.name()) ? messages.get(err.name()) : super.error2message(err);
	}
	
	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}
}