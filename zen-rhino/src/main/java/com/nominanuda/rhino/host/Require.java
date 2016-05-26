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

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Require extends ScriptableObject implements Callable {
	private static final long serialVersionUID = -4605784124862874642L;
	private ModuleRegistry registry = new ModuleRegistry();

	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		String key = (String) args[0];
		if (registry.has(key)) {
			return registry.get(key);
		} else {
			return registry.createAndStore(key, cx, thisObj, scope, args);
		}
	}

	@Override
	public String getClassName() {
		return "Function";
	}

	public void setRegistry(ModuleRegistry registry) {
		this.registry = registry;
	}

}
