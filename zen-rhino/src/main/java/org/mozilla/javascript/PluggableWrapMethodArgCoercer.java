package org.mozilla.javascript;

import com.nominanuda.rhino.ObjectCoercer;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;
import com.nominanuda.zen.obj.wrap.Wrap;

public class PluggableWrapMethodArgCoercer extends PluggableMethodArgCoercer {
	@Override
	protected Tuple2<ObjectCoercer<Object, Object, Exception>, Integer> findConvertor(Class<?> typeTo, Object valueFrom) {
		return super.findConvertor(ObjWrapper.class.isAssignableFrom(typeTo) ? Obj.class : typeTo, valueFrom); // convert to Obj if we are to return an ObjWrapper
	}
	
	@Override
	public Object coerceTypeImpl(Class<?> typeTo, Object valueFrom) {
		Object result = super.coerceTypeImpl(typeTo, valueFrom);
		return result instanceof Obj && ObjWrapper.class.isAssignableFrom(typeTo)
				? Wrap.WF.wrap((Obj) result, typeTo) // if we did it to Obj and typeTo is an ObjWrapper, wrap it
				: result;
	}
}
