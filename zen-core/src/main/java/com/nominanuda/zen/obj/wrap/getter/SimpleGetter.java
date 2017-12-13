package com.nominanuda.zen.obj.wrap.getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;
import com.nominanuda.zen.obj.wrap.Wrap;
import com.nominanuda.zen.obj.wrap.WrapType;
import com.nominanuda.zen.obj.wrap.WrapperItemFactory;


@ThreadSafe
public class SimpleGetter implements IGetter {
	public final static SimpleGetter GETTER = new SimpleGetter();
	
	private static final Map<Class<?>, Function<Obj, Object>> BUILDERS_CACHE = new HashMap<>();
	private Wrap wf;

	protected SimpleGetter() {
		// just to avoid constructors during usage from outside
	}
	
	
	@Override
	public void init(Wrap wf) {
		this.wf = wf;
	}
	
	@Override
	public boolean supports(Method getter) {
		return true;
	}
	
	@Override
	public Object extract(Obj o, Method getter) throws Throwable {
		return fromObjValue(o.get(getter.getName()), getter.getReturnType());
	}
	
	
	protected Object fromObjValue(Object v, @Nullable Class<?> type) {
		if (type == null && v != null) {
			type = v.getClass();
		}
		if (Boolean.TYPE.equals(type)) { // expected boolean (not Boolean)
			return Boolean.TRUE.equals(v); // force true/false (also when v == null)
		} else if (null == v) {
			return null;
		} else if (JsonType.isNullablePrimitive(v)) {
			if (Boolean.class.equals(type)) {
				return Boolean.TRUE.equals(v);
			} else if (Double.class.equals(type) || double.class.equals(type)) {
				return ((Number)v).doubleValue();
			} else if (Float.class.equals(type) || float.class.equals(type)) {
				return ((Number)v).floatValue();
			} else if (Integer.class.equals(type) || int.class.equals(type)) {
				return ((Number)v).intValue();
			} else if (Long.class.equals(type) || long.class.equals(type)) {
				return ((Number)v).longValue();
			} else {
				return v;
			}
		} else if (JsonType.isObj(v)) {
			Obj o = (Obj)v;
			WrapType wrapType = type.getAnnotation(WrapType.class);
			if (wrapType != null) {
				return createBuilder(wrapType, type).apply(o);
			} else if (WrapperItemFactory.class.isAssignableFrom(type)) {
				try {
					return findWrapMethod(type).invoke(null, v);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			} else if (ObjWrapper.class.isAssignableFrom(type)) { // sub object
				return wf.wrap(o, type);
			}
		}
		throw new IllegalArgumentException("cannot convert value:" + v + " to type:" + type.getName());
	}
	
	private Function<Obj, Object> createBuilder(WrapType wrapType, Class<?> type) {
		Function<Obj, Object> builder = BUILDERS_CACHE.get(type);
		if (builder == null) {
			String field = wrapType.field();
			Map<String, Class<?>> typeMap = new HashMap<>();
			String[] values = wrapType.values();
			Class<?>[] types = wrapType.types();
			for (int i = 0; i < values.length; i++) {
				typeMap.put(values[i], types[i]);
			}
			builder = (o) -> wf.wrap(o, typeMap.get(o.fetch(field)));
			BUILDERS_CACHE.put(type, builder);
		}
		return builder;
	}
	
	private Method findWrapMethod(Class<?> type) throws NoSuchMethodException {
		try {
			return type.getMethod("wrap", Obj.class);
		} catch (NoSuchMethodException e) {
			for (Class<?> ancestor : type.getInterfaces()) {
				if (WrapperItemFactory.class.isAssignableFrom(ancestor)) {
					try {
						return findWrapMethod(ancestor);
					} catch(NoSuchMethodException e1) {}
				}
			}
			throw new NoSuchMethodException();
		}
	}
}
