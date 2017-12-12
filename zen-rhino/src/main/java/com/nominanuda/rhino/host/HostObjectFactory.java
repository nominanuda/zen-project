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

import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class HostObjectFactory implements ModuleFactory {	
	private Map<String,String> hostObjects = new HashMap<String,String>();

	@SuppressWarnings("unchecked")
	public Object create(String key, Scriptable thisObj, Scriptable scope, Context context) throws Exception {
		if (hostObjects.containsKey(key)){
			String defClass = hostObjects.get(key);
			BaseFunction baseFunction = RHINO.buildClassCtor(scope,(Class<? extends Scriptable>)Class.forName(defClass),false,false);
			Scriptable scp = baseFunction.createObject(context,scope);//TODO review
			return scp == null ? baseFunction : scp;
		}
		return null;
	}

	public void setHostObjects(Map<String, String> hostObjects) {
		this.hostObjects.clear();
		this.hostObjects.putAll(hostObjects);
	}

	public void addObject(String name, String className) {
		this.hostObjects.put(name, className);
	}
}