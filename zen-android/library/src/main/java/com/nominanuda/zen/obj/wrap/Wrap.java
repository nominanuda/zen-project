package com.nominanuda.zen.obj.wrap;


import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Wrap {
	public static final Wrap WF = new Wrap();

	@SuppressWarnings("unchecked")
	public <T> T wrap(JSONObject o, Class<T> cl) {
		InvocationHandler h = new WrapperInvocationHandler(o, cl);
		return (T)Proxy.newProxyInstance(cl.getClassLoader(), new Class[] { cl }, h);
	}
	
	public <T> T wrap(Class<T> cl) {
		return wrap(null, cl);
	}

//	public <T extends ObjWrapper> T clone(T obj, Class<T> cl) {
//		return wrap(obj.unwrap().copyCast(), cl);
//	}
}