package com.nominanuda.zen.obj.wrap.getter;

import android.util.Pair;

import com.nominanuda.zen.common.Util;
import com.nominanuda.zen.obj.wrap.Cls;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class MapGetter extends SimpleGetter {
	public final static MapGetter GETTER = new MapGetter();
	
	private final static Util.Function<String, Integer> STRING2INT = s -> Integer.parseInt(s);
	private final static Util.Function<String, Long> STRING2LONG = s -> Long.parseLong(s);
	private final static Util.Function<String, String> STRING2STRING = s -> s;
	

	protected MapGetter() {
		// just to avoid constructors during usage from outside
	}
	
	
	@Override
	public boolean supports(Method getter) {
		return Map.class.isAssignableFrom(getter.getReturnType());
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Object extract(JSONObject o, Method getter) throws Throwable {
		final Class<?> type = getter.getReturnType();
		JSONObject obj = o.optJSONObject(getter.getName());
		if (obj != null) {
			Map<Object, Object> map = type.isInterface() ? new LinkedHashMap<>() : (Map<Object, Object>) type.newInstance();
			Util.Function<String, ?> keyConvertor = STRING2STRING;
			@Nullable Class<?> itemType = null;
			try {
				Pair<Class<?>, Class<?>> keyValTypes = getMapReturnComponentTypes(getter);
				Class<?> keyType = keyValTypes.first;
				if (Integer.class.equals(keyType)) {
					keyConvertor = STRING2INT;
				} else if (Long.class.equals(keyType)) {
					keyConvertor = STRING2LONG;
				}
				itemType = keyValTypes.second;
			} catch (Exception e) {
				// dynamic mode on
			}
			Iterator<String> i = obj.keys();
			while (i.hasNext()) {
				String key = i.next();
				Object val = obj.isNull(key) ? null : obj.opt(key); // as per issue https://issuetracker.google.com/issues/36924550
				map.put(keyConvertor.apply(key), fromObjValue(val, itemType));
			}
			return map;
		}
		return null;
	}


	protected Pair<Class<?>, Class<?>> getMapReturnComponentTypes(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return new Pair<Class<?>, Class<?>>(String.class, cls.value());
		}
		Type t = method.getGenericReturnType();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			Type[] actualTypeArgs = pt.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 2) {
				String name0 = actualTypeArgs[0].toString();
				String name1 = actualTypeArgs[1].toString();
				return new Pair<>(
						Class.forName(name0.substring(name0.indexOf(' ') + 1)), // remove "interface " in front of name
						Class.forName(name1.substring(name1.indexOf(' ') + 1)) // remove "interface " in front of name
				);
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}
}
