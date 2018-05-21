package com.nominanuda.rhino;

import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.obj.Arr;

public class ToArrCoercer implements ObjectCoercer<Scriptable, Arr, NoException> {
	private ScriptableConvertor convertor = new ScriptableConvertor();

	public Arr apply(Scriptable x) throws NoException {
		return convertor.fromScriptable(x).asArr();
	}

	public boolean canConvert(Object o) {
		return o != null && ScriptRuntime.isArrayObject(o);
	}
}
