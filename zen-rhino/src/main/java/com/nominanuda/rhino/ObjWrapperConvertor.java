package com.nominanuda.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.obj.wrap.ObjWrapper;

public class ObjWrapperConvertor extends StruConvertor {
	@Override
	public Scriptable convert(Context cx, Scriptable prototypeSearchScope, Object o) {
		return super.convert(cx, prototypeSearchScope, ((ObjWrapper)o).unwrap());
	}
	
	@Override
	public boolean canConvert(Object obj) {
		return obj != null && obj instanceof ObjWrapper;
	}
}
