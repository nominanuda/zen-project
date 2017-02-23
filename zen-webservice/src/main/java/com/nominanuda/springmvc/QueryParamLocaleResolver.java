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
package com.nominanuda.springmvc;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import com.nominanuda.zen.common.Check;

public class QueryParamLocaleResolver extends AbstractLocaleResolver {
	private String paramName = "lang";

	public Locale resolveLocale(HttpServletRequest request) {
		String lang = request.getParameter(paramName);
		if (lang != null) {
			Locale locale = StringUtils.parseLocaleString(lang);
			return locale;
		}
		return getDefaultLocale();
	}

	public void setLocale(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		Check.unsupportedoperation.fail();
	}

}
