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
package com.nominanuda.jsweb.host;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.nominanuda.code.Nullable;
import com.nominanuda.web.http.HttpProtocol;

public class JsHttpRequest extends ScriptableObject implements HttpProtocol {
	private static final long serialVersionUID = -7386543758365478363L;
	private static final RhinoHelper rhinoHelper = new RhinoHelper();
	protected HttpRequest req;

	public JsHttpRequest() {
	}

	public void jsConstructor(Object _req) {
		req = (HttpRequest) _req;
	}

	@Override
	public String getClassName() {
		return "HttpRequest";
	}

	public String jsGet_uri() {
		return req.getRequestLine().getUri();
	}

	public String jsGet_method() {
		return req.getRequestLine().getMethod();
	}

	//null or {name:..., value:...}
	public @Nullable Scriptable jsFunction_firstHeader(String name) {
		Context cx = Context.getCurrentContext();
		Header h = req.getFirstHeader(name);
		return h == null ? null : makeJsHeader(cx, h);
	}

	//[] or [{name:..., value:...},{name:..., value:...}]
	public @Nullable Scriptable jsFunction_headers(String name) {
		Context cx = Context.getCurrentContext();
		Header[] hs = req.getHeaders(name);
		Scriptable result = rhinoHelper.newArray(cx, getParentScope());
		int i = 0;
		for(Header h : hs) {
			Scriptable jsh = makeJsHeader(cx, h);
			rhinoHelper.putProperty(result, i++, jsh);
		}
		return result;
	}

	private Scriptable makeJsHeader(Context cx, Header h) {
		Scriptable jsh = rhinoHelper.newObject(cx, getParentScope());
		rhinoHelper.putProperty(jsh, "name", h.getName());
		rhinoHelper.putProperty(jsh, "value", h.getValue());
		return jsh;
	}
}
