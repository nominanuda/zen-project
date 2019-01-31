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
package com.nominanuda.springmvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpRequest;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.nominanuda.rhino.host.ModuleFactory;
import com.nominanuda.rhino.host.ModuleRegistry;
import com.nominanuda.rhino.host.Require;
import com.nominanuda.rhino.host.SourceModuleFactory;
import com.nominanuda.webapp.Helper;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class WebappRhinoHandler extends CompilingRhinoHandler {
	private final static String KEY_WEBAPP_HELPER = "__WEBAPP_HELPER";
	private final static String KEY_WEBAPP_REQUIRE = "__WEBAPP_REQUIRE";
	private final Map<String, Object> hardParams, hostObjects;
	
	public WebappRhinoHandler(Map<String, Object> hardParams, Map<String, Object> hostObjects) {
		this.hardParams = hardParams != null ? hardParams : new HashMap<>();
		this.hostObjects = hostObjects != null ? hostObjects : new HashMap<>();
	}
	
	@Override
	public void init() {
		ModuleRegistry reg = new ModuleRegistry();
		reg.setModuleFactories(new ArrayList<ModuleFactory>() {{
			add(new SourceModuleFactory());
		}});
		reg.setCache(true);
		Require req = new Require();
		req.setRegistry(reg);
		hostObjects.put(KEY_WEBAPP_REQUIRE, req);
		hostObjects.put(KEY_WEBAPP_HELPER, new Helper(sitemap));
		super.init();
	}
	
	@Override
	protected String calcScriptUri(Stru cmd, HttpRequest request) throws IOException {
		// TODO merge here hardParams with cmd coming from request, instead of doing it in executeFunction(...)?
		return super.calcScriptUri(cmd, request);
	}
	
	@Override
	protected void evaluateScript(Context cx, Scriptable controllerScope, String uri) throws IOException {
		for (Entry<String, Object> entry : hostObjects.entrySet()) {
			((ScriptableObject)controllerScope).defineProperty(entry.getKey(), entry.getValue(), ScriptableObject.DONTENUM | ScriptableObject.READONLY);
		}
		super.evaluateScript(cx, controllerScope, uri);
	}
	
	@Override
	protected Object executeFunction(Context cx, Scriptable controllerScope, String function, Stru cmd, HttpRequest request) {
		for (Entry<String, Object> entry : hardParams.entrySet()) {
			((Obj)cmd).put(entry.getKey(), entry.getValue());
		}
		return super.executeFunction(cx, controllerScope, function, cmd, request);
	}
}
