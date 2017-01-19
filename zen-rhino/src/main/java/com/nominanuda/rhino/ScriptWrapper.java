package com.nominanuda.rhino;

import static com.nominanuda.rhino.DataStructScriptableConvertor.DSS_CONVERTOR;
import static org.mozilla.javascript.RhinoHelper.RHINO;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.rhino.IScriptSource.IScript;
import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;

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
			jsArgs[i] = (arg != null && arg instanceof DataStruct) ? DSS_CONVERTOR.toScriptable(cx, (DataStruct)arg, scope) : arg;
		}
		return RHINO.callFunctionInScope(cx, scope, function, jsArgs);
	}
	
	@Override
	public DataStruct callForDataStruct(String function, Object... args) {
		Scriptable result = (Scriptable) call(function, args);
		return result != null ? DSS_CONVERTOR.fromScriptable(result) : null;
	}
	@Override
	public DataArray callForDataArray(String function, Object... args) {
		DataStruct result = callForDataStruct(function, args);
		return result != null ? result.asArray() : null;
	}
	@Override
	public DataObject callForDataObject(String function, Object... args) {
		DataStruct result = callForDataStruct(function, args);
		return result != null ? result.asObject() : null;
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
