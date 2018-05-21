package com.nominanuda.jsweb.host;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.nominanuda.rhino.ScriptableConvertor;
import com.nominanuda.springmvc.Sitemap;
import com.nominanuda.zen.obj.Obj;

public class Location extends ScriptableObject implements Callable {
	private static final long serialVersionUID = 3193397575047488553L;
	private ScriptableConvertor dataStructScriptableConvertor = new ScriptableConvertor();
	private Sitemap sitemap;

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		String id = (String) args[0];
		Scriptable params = (args.length > 1 ? (Scriptable) args[1] : null);
		return sitemap.getUrl(id, params != null ? (Obj) dataStructScriptableConvertor.fromScriptable(params) : null);
	}

	@Override
	public String getClassName() {
		return "Function";
	}
	
	public void setSitemap(Sitemap s) {
		sitemap = s;
	}
}
