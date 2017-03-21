package com.nominanuda.rhino;

import static com.nominanuda.rhino.StruScriptableConvertor.DSS_CONVERTOR;
import static org.mozilla.javascript.RhinoHelper.RHINO;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.rhino.IScriptSource.IScript;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class ScriptWrapper implements IScript {
	private final Context cx;
	private final Scriptable scope;
	private final String source;
	
	ScriptWrapper(Context cx, Scriptable scope, String source) {
		this.cx = cx;
		this.scope = scope;
		this.source = source;
	}
	
	@Override
	public Object call(String function, Object... args) {
		Object[] jsArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			jsArgs[i] = (arg != null && arg instanceof Stru) ? DSS_CONVERTOR.toScriptable(cx, (Stru)arg, scope) : arg;
		}
		return RHINO.callFunctionInScope(cx, scope, function, jsArgs);
	}
	
	@Override
	public Stru callForDataStruct(String function, Object... args) {
		Scriptable result = (Scriptable) call(function, args);
		return result != null ? DSS_CONVERTOR.fromScriptable(result) : null;
	}
	@Override
	public Arr callForDataArray(String function, Object... args) {
		Stru result = callForDataStruct(function, args);
		return result != null ? result.asArr() : null;
	}
	@Override
	public Obj callForDataObject(String function, Object... args) {
		Stru result = callForDataStruct(function, args);
		return result != null ? result.asObj() : null;
	}
	
	@Override
	public String source() {
		return source;
	}
	
	@Override
	public void close() {
		Context.exit();
	}
}
