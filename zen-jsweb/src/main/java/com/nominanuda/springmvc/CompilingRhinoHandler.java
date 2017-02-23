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

import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SpringScopeFactory;

import com.nominanuda.zen.common.Tuple2;

public class CompilingRhinoHandler extends RhinoHandler {
	private Map<String, Script> scriptCache = new HashMap<String, Script>();
	private boolean cache = true;
	
	
	public CompilingRhinoHandler(Map<String, Object> hardParams, Map<String, Object> hostObjects) {
		super(hardParams, hostObjects);
	}
	public CompilingRhinoHandler() {
		super();
	}
	
	
	@Override
	protected void evaluateScript(Context cx, Scriptable controllerScope, String uri) throws IOException {
		if (cache) {
			Script s1 = getOrCompile(cx, uri);
			RHINO.evaluateScript(s1, cx, controllerScope);
		} else {
			super.evaluateScript(cx, controllerScope, uri);
		}
	}
	
	private Script getOrCompile(Context cx, String uri) throws IOException {
		if (scriptCache.containsKey(uri)) {
			return scriptCache.get(uri);
		} else {
			Tuple2<String,Reader> script = getSource(uri);
			Script s = RHINO.compileScript(script.get1(), script.get0(), null, cx);
			scriptCache.put(uri, s);
			return s;
		}
	}
	
	protected void setCache(boolean cache) {
		this.cache = cache;
	}
	
	
	
	/* setters */
	
	@Override
	public void setSpringScopeFactory(SpringScopeFactory scopeFactory) {
		super.setSpringScopeFactory(scopeFactory);
		setCache(scopeFactory.getCache());
	}
	
	public void setDevelMode(boolean develMode) {
		// just used to force cache off, not to put it back on (or it will race with setCache)
		if (develMode) {
			cache = false;
		}
	}
}
