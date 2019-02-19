package com.nominanuda.zen.obj.wrap.getter;

import com.nominanuda.zen.obj.wrap.Cls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class CollectionGetter extends SimpleGetter {
	public static final CollectionGetter GETTER = new CollectionGetter();

	protected CollectionGetter() {
		// just to avoid constructors during usage from outside
	}


	@Override
	public boolean supports(Method getter) {
		return Collection.class.isAssignableFrom(getter.getReturnType());
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object extract(JSONObject o, Method getter) throws Throwable {
		final Class<?> type = getter.getReturnType();
		JSONArray arr = o.optJSONArray(getter.getName());
		if (arr != null) {
			Collection<Object> coll = type.isInterface() ? new LinkedList<>() : (Collection<Object>) type.newInstance();
			Class<?> itemType = null;
			try {
				itemType = getCollectionReturnComponentType(getter);
			} catch (Exception e) {
				// dynamic mode on
			}
			for (int i = 0, l = arr.length(); i < l; i++) {
				Object val = arr.isNull(i) ? null : arr.opt(i); // as per issue https://issuetracker.google.com/issues/36924550
				coll.add(fromObjValue(val, itemType));
			}
			return coll;
		}
		return null;
	}

	protected Class<?> getCollectionReturnComponentType(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return cls.value();
		}
		Type t = method.getGenericReturnType();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			Type[] actualTypeArgs = pt.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 1) {
				String name = actualTypeArgs[0].toString();
				return Class.forName(name.substring(name.indexOf(' ') + 1)); // remove "interface " in front of name
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}
}
