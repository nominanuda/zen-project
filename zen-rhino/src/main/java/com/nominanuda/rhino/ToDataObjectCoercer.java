package com.nominanuda.rhino;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.lang.NoException;
import com.nominanuda.lang.ObjectConvertor;

public class ToDataObjectCoercer implements ObjectConvertor<Scriptable, DataObject, NoException> {
	private DataStructScriptableConvertor convertor = new DataStructScriptableConvertor();

	public DataObject apply(Scriptable x) throws NoException {
		return convertor.fromScriptable(x).asObject();
	}

	public boolean canConvert(Object o) {
		return o != null && o instanceof NativeObject;
	}
}
