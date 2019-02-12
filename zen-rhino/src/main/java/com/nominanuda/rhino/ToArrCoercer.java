package com.nominanuda.rhino;

import static com.nominanuda.rhino.ScriptableConvertor.SCONVERTOR;

import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.obj.Arr;

public class ToArrCoercer implements ObjectCoercer<Scriptable, Arr, NoException> {
	@Override
	public Arr apply(Scriptable x) throws NoException {
		return SCONVERTOR.fromScriptable(x).asArr();
	}

	@Override
	public boolean canConvert(Object o) {
		return o != null && ScriptRuntime.isArrayObject(o);
	}
}
