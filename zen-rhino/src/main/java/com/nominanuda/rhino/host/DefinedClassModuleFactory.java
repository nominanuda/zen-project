package com.nominanuda.rhino.host;

import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class DefinedClassModuleFactory implements ModuleFactory {
	private Map<String,String> map = new HashMap<String,String>();
	
	public DefinedClassModuleFactory(Map<String,String> map){
		this.map.putAll(map);
	}
	
	public Object create(String key, Scriptable thisObj, Scriptable scope, Context context) throws Exception {
		String defClass = map.get(key);
		if (defClass == null) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			Object res = RHINO.buildClassCtor(scope,(Class<? extends Scriptable>)Class.forName(defClass.trim()),false,false);
			if(res instanceof JavaJsHostObject) {
				String script = ((JavaJsHostObject)res).getJsScript();
				RHINO.evaluateReader(context, new StringReader(script), defClass, scope);
			}
			return res;
		}
	}

}