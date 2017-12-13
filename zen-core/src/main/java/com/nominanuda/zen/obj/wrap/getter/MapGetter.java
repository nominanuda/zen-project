package com.nominanuda.zen.obj.wrap.getter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.Cls;


@ThreadSafe
public class MapGetter extends SimpleGetter {
	public final static MapGetter GETTER = new MapGetter();
	
	private final static Function<String, Integer> STRING2INT = s -> Integer.parseInt(s);
	private final static Function<String, Long> STRING2LONG = s -> Long.parseLong(s);
	private final static Function<String, String> STRING2STRING = s -> s;
	

	protected MapGetter() {
		// just to avoid constructors during usage from outside
	}
	
	
	@Override
	public boolean supports(Method getter) {
		return Map.class.isAssignableFrom(getter.getReturnType());
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Object extract(Obj o, Method getter) throws Throwable {
		final Class<?> type = getter.getReturnType();
		Obj obj = o.getObj(getter.getName());
		if (obj != null) {
			if (type.equals(Obj.class)) {
				return obj;
			}
			Map<Object, Object> map = type.isInterface() ? new LinkedHashMap<>() : (Map<Object, Object>) type.newInstance();
			Function<String, ?> keyConvertor = STRING2STRING;
			@Nullable Class<?> itemType = null;
			try {
				Tuple2<Class<?>, Class<?>> keyValTypes = getMapReturnComponentTypes(getter);
				Class<?> keyType = keyValTypes.get0();
				if (Integer.class.equals(keyType)) {
					keyConvertor = STRING2INT;
				} else if (Long.class.equals(keyType)) {
					keyConvertor = STRING2LONG;
				}
				itemType = keyValTypes.get1();
			} catch (Exception e) {
				// dynamic mode on
			}
			for (String key : obj.keySet()) {
				map.put(keyConvertor.apply(key), fromObjValue(obj.get(key), itemType));
			}
			return map;
		}
		return null;
	}

	protected Tuple2<Class<?>, Class<?>> getMapReturnComponentTypes(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return new Tuple2<>(String.class, cls.value());
		}
		Type t1 = method.getGenericReturnType();
		if (t1 instanceof ParameterizedType) {
			ParameterizedType t2 = (ParameterizedType)t1;
			Type[] actualTypeArgs = t2.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 2) {
				return new Tuple2<>(
					Class.forName(actualTypeArgs[0].getTypeName()),
					Class.forName(actualTypeArgs[1].getTypeName()));
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}
}
