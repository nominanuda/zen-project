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

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.entity.StringEntity;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.CommandRequestHandler;

public class SoyJsTemplateServer implements CommandRequestHandler, HttpProtocol {
	private SoySource soySource;

	public void setSoySource(SoySource soySource) {
		this.soySource = soySource;
	}

	public Object handle(DataStruct cmd, HttpRequest request)
			throws Exception {
		String lang = ((DataObject)cmd).getString("lang");
		String tpl = soySource.getJsTemplate(getTemplateName(cmd, request), lang);
		StringEntity entity = new StringEntity(tpl, CT_TEXT_JAVASCRIPT, UTF_8);
		return entity;
	}

	private String getTemplateName(DataStruct cmd, HttpRequest request) {
		String reqPath = URI.create(request.getRequestLine().getUri()).getPath();
		return reqPath.substring(reqPath.lastIndexOf('/') + 1, reqPath.length() - ".js".length());
	}

}
