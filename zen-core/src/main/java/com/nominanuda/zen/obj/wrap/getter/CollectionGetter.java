package com.nominanuda.zen.obj.wrap.getter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.Cls;


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
	public Object extract(Obj o, Method getter) throws Throwable {
		final Class<?> type = getter.getReturnType();
		Arr arr = o.getArr(getter.getName());
		if (arr != null) {
			if (type.equals(Arr.class)) {
				return arr;
			}
			Collection<Object> coll = type.isInterface() ? new LinkedList<>() : (Collection<Object>) type.newInstance();
			@Nullable Class<?> itemType = null;
			try {
				itemType = getCollectionReturnComponentType(getter);
			} catch (Exception e) {
				// dynamic mode on
			}
			for (Object v : arr) {
				coll.add(fromObjValue(v, itemType));
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
		Type t1 = method.getGenericReturnType();
		if (t1 instanceof ParameterizedType) {
			ParameterizedType t2 = (ParameterizedType)t1;
			Type[] actualTypeArgs = t2.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 1) {
				return Class.forName(actualTypeArgs[0].getTypeName());
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}
}
