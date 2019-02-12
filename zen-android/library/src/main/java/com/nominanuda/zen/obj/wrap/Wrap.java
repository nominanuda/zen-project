package com.nominanuda.zen.obj.wrap;


import com.nominanuda.zen.obj.wrap.getter.CollectionGetter;
import com.nominanuda.zen.obj.wrap.getter.IGetter;
import com.nominanuda.zen.obj.wrap.getter.MapGetter;
import com.nominanuda.zen.obj.wrap.getter.SimpleGetter;

import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Wrap {
	public static final Wrap WF = new Wrap();
	private final IGetter[] getters;


	public Wrap(IGetter...getters) {
		for (IGetter getter : this.getters = getters) {
			getter.init(this);
		}
	}
	public Wrap() {
		this(MapGetter.GETTER, CollectionGetter.GETTER, SimpleGetter.GETTER);
	}


	@SuppressWarnings("unchecked")
	public <T> T wrap(JSONObject o, Class<T> cl) {
		InvocationHandler h = new WrapperInvocationHandler(o, cl, getters);
		return (T)Proxy.newProxyInstance(cl.getClassLoader(), new Class[] { cl }, h);
	}
	
	public <T> T wrap(Class<T> cl) {
		return wrap(null, cl);
	}

//	public <T extends ObjWrapper> T clone(T obj, Class<T> cl) {
//		return wrap(obj.unwrap().copyCast(), cl);
//	}
}