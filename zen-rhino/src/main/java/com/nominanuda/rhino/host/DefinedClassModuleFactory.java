package com.nominanuda.rhino.host;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.Scriptable;

public class DefinedClassModuleFactory implements ModuleFactory {
	private static final RhinoHelper rhino = new RhinoHelper();
	private Map<String,String> map = new HashMap<String,String>();
	
	public DefinedClassModuleFactory(Map<String,String> map){
		this.map.putAll(map);
	}
	
	public Object create(String key, Scriptable thisObj, Scriptable scope, Context context) throws Exception {
		String defClass = map.get(key);
		if(defClass == null) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			Object res = rhino.buildClassCtor(scope,(Class<? extends Scriptable>)Class.forName(defClass),false,false);
			if(res instanceof JavaJsHostObject) {
				String script = ((JavaJsHostObject)res).getJsScript();
				rhino.evaluateReader(context, new StringReader(script), defClass, scope);
			}
			return res;
		}
	}

}