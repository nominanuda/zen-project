package com.nominanuda.rhino.host;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

abstract class AbsCallback<RET> extends ScriptableObject implements Callable {
	private static final long serialVersionUID = 18089380274748L;

	abstract RET call(Object... args);
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		return call(args);
	}
	
	@Override
	public String getClassName() {
		return "Function";
	}
}
