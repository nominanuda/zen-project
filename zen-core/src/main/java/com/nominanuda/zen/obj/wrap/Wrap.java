package com.nominanuda.zen.obj.wrap;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.getter.CollectionGetter;
import com.nominanuda.zen.obj.wrap.getter.IGetter;
import com.nominanuda.zen.obj.wrap.getter.MapGetter;
import com.nominanuda.zen.obj.wrap.getter.SimpleGetter;

public class Wrap {
	public static final Wrap WF = new Wrap();
	private static final Map<Class<?>, Function<Obj, Object>> BUILDERS_CACHE = new HashMap<>();
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
	public <T> T wrap(Obj o, Class<T> cl) {
		if (o == null) {
			o = Obj.make();
		}
		InvocationHandler h = new WrapperInvocationHandler(o, cl, getters);
		WrapType wrapType = cl.getAnnotation(WrapType.class);
		return (T) (wrapType != null
			? createBuilder(wrapType, cl).apply(o)
			: Proxy.newProxyInstance(cl.getClassLoader(), new Class[] { cl }, h));
	}
	
	public <T> T wrap(Class<T> cl) {
		return wrap(null, cl);
	}

	public <T extends ObjWrapper> T clone(T obj, Class<T> cl) {
		return wrap(obj.unwrap().copyCast(), cl);
	}
	
	
	private Function<Obj, Object> createBuilder(WrapType wrapType, Class<?> type) {
		Function<Obj, Object> builder = BUILDERS_CACHE.get(type);
		if (builder == null) {
			Map<String, Class<?>> typeMap = new HashMap<>();
			String[] values = wrapType.values();
			Class<?>[] types = wrapType.types();
			for (int i = 0; i < values.length; i++) {
				typeMap.put(values[i], types[i]);
			}
			String field = wrapType.field();
			builder = o -> wrap(o, typeMap.get(o.fetch(field)));
			BUILDERS_CACHE.put(type, builder);
		}
		return builder;
	}
}