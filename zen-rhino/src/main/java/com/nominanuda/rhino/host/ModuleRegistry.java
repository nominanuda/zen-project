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
package com.nominanuda.rhino.host;

import static com.nominanuda.zen.common.Ex.EX;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;

public class ModuleRegistry {
	private Map<String,Object> resolvedModules = new HashMap<String, Object>();
	private List<ModuleFactory> moduleFactories = new LinkedList<ModuleFactory>();
	private boolean cache = true;

	public boolean has(String key) {
		return cache ? resolvedModules.containsKey(key) : false;
	}

	public Object get(String key) {
		return cache ? resolvedModules.get(key) : null;
	}

	public Object createAndStore(String key, Context cx, Scriptable thisObj, Scriptable scope, Object... args) {
		try {
			Object o = createAndForget(key, cx, thisObj, scope, args);
			resolvedModules.put(key, o);
			return o;
		} catch (Exception e) {
			throw new EvaluatorException(EX.toStackTrace(e));
		}
	}
	
	public Object createAndForget(String key, Context cx, Scriptable thisObj, Scriptable scope, Object... args) {
		try {
			Object result = null;
			for (ModuleFactory f : moduleFactories) {
				result = f.create(key, thisObj, scope, cx);
				if (result != null) { 
					return result;
				}
			}
			throw new IllegalArgumentException("require could not find a module named " + key);
		} catch (Exception e) {
			throw new EvaluatorException(EX.toStackTrace(e));
		}
	}
	
	
	/* setters */

	public void setModuleFactories(List<? extends ModuleFactory> moduleFactories) {
		this.moduleFactories.clear();
		this.moduleFactories.addAll(moduleFactories);
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}
}
