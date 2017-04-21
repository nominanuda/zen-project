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

import static com.nominanuda.zen.io.Uris.URIS;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.CommandRequestHandler;
import com.nominanuda.web.mvc.ObjURISpec;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class SoyJsTemplateServer implements CommandRequestHandler, HttpProtocol {
	private final static String PARAM_TEMPLATE = "template";
	private final Map<String, Object> hardParams;
	private ObjURISpec template;
	private SoySource soySource;
	private String prefix = "";
	
	public SoyJsTemplateServer() {
		hardParams = new HashMap<String, Object>();
	}
	public SoyJsTemplateServer(Map<String, Object> hardParams) {
		this.hardParams = hardParams;
	}

	public Object handle(Stru cmd, HttpRequest request) throws Exception {
		Obj json = cmd.asObj();
		for (String key : hardParams.keySet()) {
			json.put(key, hardParams.get(key));
		}
		// also removes any "classpath:", "classpath:/" in front of template url, in case some configs (wrongly) put it there
		String tpl = soySource.getJsTemplate(getTemplateName(json, request).replaceAll("^\\w+:/?", ""), json.getStr("lang"));
		return new StringEntity(tpl, ContentType.create(CT_TEXT_JAVASCRIPT, CS_UTF_8));
	}

	private String getTemplateName(Obj json, HttpRequest request) {
		if (template != null) { // use json to resolve the uritemplate, if present
			return template.template(json);
		}
		if (json.exists(PARAM_TEMPLATE)) { // use the request param "template", if present
			return URIS.pathJoin(prefix, json.getStr(PARAM_TEMPLATE));
		}
		// extract template name from last part of request path
		String reqPath = URI.create(request.getRequestLine().getUri()).getPath();
		return reqPath.substring(reqPath.lastIndexOf('/') + 1, reqPath.length() - ".js".length());
	}
	
	
	/* setters */
	
	public void setTemplate(String spec) {
		this.template = new ObjURISpec(spec);
	}
	
	public void setSoySource(SoySource soySource) {
		this.soySource = soySource;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
