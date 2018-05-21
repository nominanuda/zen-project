package com.nominanuda.rhino;

import static com.nominanuda.rhino.ScriptableConvertor.SCONVERTOR;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ToScriptableConvertor;

public class SeqConvertor implements ToScriptableConvertor {
	@Override
	public Scriptable convert(Context cx, Scriptable prototypeSearchScope, Object o) {
		if (o instanceof List) {
			return SCONVERTOR.listToScriptable(cx, (List<?>)o, prototypeSearchScope);
		}
		if (o instanceof Map) {
			return SCONVERTOR.mapToScriptable(cx, (Map<?, ?>)o, prototypeSearchScope);
		}
		throw new IllegalStateException();
	}
	
	@Override
	public boolean canConvert(Object obj) {
		return obj instanceof List
			|| obj instanceof Map;
	}
}
