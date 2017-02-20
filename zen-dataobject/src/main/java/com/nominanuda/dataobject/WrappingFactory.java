package com.nominanuda.dataobject;


import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class WrappingFactory {
	public static final WrappingFactory WF = new WrappingFactory();

	@SuppressWarnings("unchecked")
	public <T> T wrap(DataObject o, Class<T> cl) {
		InvocationHandler h = new WrapperInvocationHandler(o, cl);
		return (T)Proxy.newProxyInstance(cl.getClassLoader(), new Class[] { cl }, h);
	}
	
	public <T> T wrap(Class<T> cl) {
		return wrap(null, cl);
	}

	public <T extends DataObjectWrapper> T clone(T obj, Class<T> cl) {
		return wrap(STRUCT.clone(obj.unwrap()), cl);
	}
}