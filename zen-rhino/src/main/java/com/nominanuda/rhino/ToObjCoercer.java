package com.nominanuda.rhino;

import static com.nominanuda.rhino.ScriptableConvertor.SCONVERTOR;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.obj.Obj;

public class ToObjCoercer implements ObjectCoercer<Scriptable, Obj, NoException> {
	@Override
	public Obj apply(Scriptable x) throws NoException {
		return SCONVERTOR.fromScriptable(x).asObj();
	}

	@Override
	public boolean canConvert(Object o) {
		return o != null && o instanceof NativeObject;
	}
}
