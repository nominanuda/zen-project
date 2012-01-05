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
package com.nominanuda.springsoy;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.google.template.soy.tofu.SoyTofu;
import com.nominanuda.web.http.HttpProtocol;

public class SoyViewResolver implements ViewResolver {
	private SoySource soySource;
	
	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		return new SoyView(viewName, soySource.getSoyTofu(locale.getLanguage()));
	}

	public void setSoySource(SoySource soySource) {
		this.soySource = soySource;
	}

	public static class SoyView implements View {
		private final SoyTofu tofu;
		private final String name;

		public SoyView(String name, SoyTofu tofu) {
			this.name = name;
			this.tofu = tofu;
		}

		public String getContentType() {
			return "text/html;charset=UTF-8";
		}

		public void render(Map<String, ?> model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			byte[] b = tofu.newRenderer(name)
						.setData(model)
						.render()
						.getBytes(HttpProtocol.CS_UTF_8);
			response.setContentType(getContentType());
			response.setContentLength(b.length);
			response.getOutputStream().write(b);
		}

	}
}
