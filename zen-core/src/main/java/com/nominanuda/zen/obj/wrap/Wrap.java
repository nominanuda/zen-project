package com.nominanuda.zen.obj.wrap;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.nominanuda.zen.obj.Obj;

public class Wrap {
	public static final Wrap WF = new Wrap();

	@SuppressWarnings("unchecked")
	public <T> T wrap(Obj o, Class<T> cl) {
		InvocationHandler h = new WrapperInvocationHandler(o, cl);
		return (T)Proxy.newProxyInstance(cl.getClassLoader(), new Class[] { cl }, h);
	}
	
	public <T> T wrap(Class<T> cl) {
		return wrap(null, cl);
	}

	public <T extends ObjWrapper> T clone(T obj, Class<T> cl) {
		return wrap(obj.unwrap().copyCast(), cl);
	}
}