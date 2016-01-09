package com.nominanuda.rhino;

import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.lang.NoException;
import com.nominanuda.lang.ObjectConvertor;

public class ToDataArrayCoercer implements ObjectConvertor<Scriptable, DataArray, NoException> {
	private DataStructScriptableConvertor convertor = new DataStructScriptableConvertor();

	public DataArray apply(Scriptable x) throws NoException {
		return convertor.fromScriptable(x).asArray();
	}

	public boolean canConvert(Object o) {
		return o != null && ScriptRuntime.isArrayObject(o);
	}
}
