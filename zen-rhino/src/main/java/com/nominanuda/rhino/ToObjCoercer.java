package com.nominanuda.rhino;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.obj.Obj;

public class ToObjCoercer implements ObjectCoercer<Scriptable, Obj, NoException> {
	private StruScriptableConvertor convertor = new StruScriptableConvertor();

	public Obj apply(Scriptable x) throws NoException {
		return convertor.fromScriptable(x).asObj();
	}

	public boolean canConvert(Object o) {
		return o != null && o instanceof NativeObject;
	}
}
