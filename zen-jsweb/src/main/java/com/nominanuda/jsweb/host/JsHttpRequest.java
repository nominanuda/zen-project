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

import static com.nominanuda.rhino.ScriptableConvertor.SCONVERTOR;
import static com.nominanuda.zen.oio.OioUtils.IO;
import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.zen.obj.Obj;

public class JsHttpRequest extends ScriptableObject implements HttpProtocol, Wrapper {
	private static final long serialVersionUID = -7386543758365478363L;
	protected Obj extra;
	protected HttpRequest req;

	@Override
	public Object unwrap() {
		return req;
	}

	public void jsConstructor(Object _req, Object _extra) {
		req = (HttpRequest) _req;
		extra = _extra instanceof Obj ? (Obj) _extra : null;
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
		Header h = req.getFirstHeader(name);
		return h == null ? null : makeJsHeader(Context.getCurrentContext(), h);
	}
	
	public @Nullable String jsFunction_firstHeaderValue(String name, Object defValue) {
		Header h = req.getFirstHeader(name);
		return h == null ? defValue == null || defValue instanceof Undefined ? "" : defValue.toString() : h.getValue();
	}

	//[] or [{name:..., value:...},{name:..., value:...}]
	public @Nullable Scriptable jsFunction_headers(String name) {
		Context cx = Context.getCurrentContext();
		Header[] hs = req.getHeaders(name);
		Scriptable result = RHINO.newArray(cx, getParentScope());
		int i = 0;
		for (Header h : hs) {
			Scriptable jsh = makeJsHeader(cx, h);
			RHINO.putProperty(result, i++, jsh);
		}
		return result;
	}

	private Scriptable makeJsHeader(Context cx, Header h) {
		Scriptable jsh = RHINO.newObject(cx, getParentScope());
		RHINO.putProperty(jsh, "name", h.getName());
		RHINO.putProperty(jsh, "value", h.getValue());
		return jsh;
	}

	//TODO don't use it
	public @Nullable String jsFunction_stringEntity() throws IOException {
		HttpCoreHelper httpHelper = new HttpCoreHelper();
		if (httpHelper.hasEntity(req)) {
			HttpEntity entity = ((HttpEntityEnclosingRequest)req).getEntity();
			return IO.readAndCloseUtf8(entity.getContent());
		} else {
			return null;
		}
	}
	
	public @Nullable Scriptable jsGet_extra() {
		return extra != null ? SCONVERTOR.struToScriptable(Context.getCurrentContext(), extra, getParentScope()) : null;
	}
}
