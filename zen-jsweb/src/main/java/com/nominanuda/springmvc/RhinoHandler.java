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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.entity.BufferedHttpEntity;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.ScopeFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.jsweb.host.JsHttpRequest;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.rhino.DataStructScriptableConvertor;
import com.nominanuda.urispec.URISpec;
import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.mvc.CommandRequestHandler;
import com.nominanuda.web.mvc.DataObjectURISpec;

public class RhinoHandler implements CommandRequestHandler {
	private static final HttpCoreHelper httpCore = new HttpCoreHelper();
	private static final DataStructHelper structHelper = new DataStructHelper();
	private String function = "handle";
	protected static final RhinoHelper rhino = new RhinoHelper();
	private URISpec<DataObject> uriSpec;
	private RhinoEmbedding rhinoEmbedding;
	//private ScriptableObject rootScope;
	private DataStructScriptableConvertor dataStructScriptableConvertor = new DataStructScriptableConvertor();
	private boolean allowJavaPackageAccess = true;//TODO security turn to false default policy
	private boolean mergeGetAndPostFormParams = true;//TODO 
	private ScriptableObject cachedScope;
	private ScopeFactory scopeFactory;

	@Override
	public DataStruct<?> handle(DataStruct<?> cmd, HttpRequest request)
			throws Exception {
		Context cx = rhinoEmbedding.enterContext();
		try {
			if(httpCore.hasEntity(request)) {
				HttpEntityEnclosingRequest r = (HttpEntityEnclosingRequest)request;
				r.setEntity(new BufferedHttpEntity(r.getEntity()));
			}
			Scriptable controllerScope = buildScope(cx);
			if(mergeGetAndPostFormParams ) {
				DataObject cmdFromReq = (DataObject)httpCore.getQueryParams(request);
				if(request instanceof HttpEntityEnclosingRequest) {
					DataObject cmdFromFormPost = (DataObject)parseEntityWithDefaultUtf8(
							((HttpEntityEnclosingRequest)request).getEntity());
					structHelper.copyPush(cmdFromFormPost, cmdFromReq);
				}
				//TODO 
				//here request params hide uriparams  
				structHelper.copyOverwite((DataObject)cmd, cmdFromReq);
				cmd = cmdFromReq;
			}

			Scriptable jsCmd = dataStructScriptableConvertor.toScriptable(cx, cmd, controllerScope);
			JsHttpRequest jsReq = (JsHttpRequest)cx.newObject(
					controllerScope, "HttpRequest", new Object[] {request});

			String scriptUri = calcScriptUri(cmd, request);
			evaluateScript(cx, controllerScope, scriptUri);
			Object res = rhino.callFunctionInScope(cx, controllerScope, function,
					new Object[] {jsCmd, jsReq});
			DataStruct<?> ds = dataStructScriptableConvertor.fromScriptable((Scriptable)res);
			return ds;
		} finally {
			Context.exit();
		}
	}
	protected void evaluateScript(Context cx, Scriptable controllerScope,
			String scriptUri) throws IOException {
		Tuple2<String,Reader> script = getSource(scriptUri);
		String jsLocation = script.get0();
		Reader src = script.get1();
		rhino.evaluateReader(cx, src, jsLocation, controllerScope);
	}
	private DataStruct<?> parseEntityWithDefaultUtf8(final HttpEntity entity) throws IOException {
		List<NameValuePair> pairs = httpCore.parseEntityWithDefaultUtf8(entity);
		return httpCore.toDataStruct(pairs);
	}

	protected String calcScriptUri(DataStruct<?> cmd, HttpRequest request) throws IOException {
		String uri = uriSpec.template((DataObject)cmd);
		return uri;
	}

	protected Tuple2<String, Reader> getSource(String uri) throws IOException {
		return new Tuple2<String, Reader>(
				uri,
				new InputStreamReader(new URL(uri).openStream(), "UTF-8"));
	}

	public void setSpec(String uriSpec) {
		this.uriSpec = new DataObjectURISpec(uriSpec);
	}

	private Scriptable buildScope(Context cx) throws Exception {
		if(scopeFactory == null) {
			if(cachedScope == null) {
				cachedScope = rhino.createTopScope(cx, allowJavaPackageAccess );
			}
			return rhino.protocloneScriptable(cx, cachedScope);
		} else {
			return scopeFactory.createInContext(cx);
		}
	}

	public void setRhinoEmbedding(RhinoEmbedding rhinoEmbedding) {
		this.rhinoEmbedding = rhinoEmbedding;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public void setAllowJavaPackageAccess(boolean allowJavaPackageAccess) {
		this.allowJavaPackageAccess = allowJavaPackageAccess;
	}
	public void setMergeGetAndPostFormParams(boolean mergeGetAndPostFormParams) {
		this.mergeGetAndPostFormParams = mergeGetAndPostFormParams;
	}
	public void setScopeFactory(ScopeFactory scopeFactory) {
		this.scopeFactory = scopeFactory;
	}
}
