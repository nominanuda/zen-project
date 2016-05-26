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

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.lang.ObjectFactory;

public class JavaObjectFactory implements ModuleFactory {
	private Map<String, Object> registry = new HashMap<String, Object>();

	public JavaObjectFactory(Map<String, Object> javaObjMap) {
		registry.putAll(javaObjMap);
	}

	public Object create(String key, Scriptable thisObj, Scriptable scope, Context context) throws Exception {
		Object registryObject = registry.get(key);
		return registryObject == null ? null : instantiate(registryObject);
	}

	public void putObject(String key, Object value) {
		registry.put(key, value);
	}

	private Object instantiate(Object registryObject) {
		return registryObject instanceof ObjectFactory<?>
			? ((ObjectFactory<?>)registryObject).getObject()
			: registryObject;
	}
}
