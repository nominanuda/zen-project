/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.zen.obj.wrap;

import android.util.Pair;

import com.nominanuda.zen.common.Util;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

import org.json.JSONException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.nominanuda.zen.obj.wrap.Wrap.WF;
import static java.util.Arrays.asList;

class WrapperInvocationHandler implements InvocationHandler {
	private final Obj o;
	private final Set<Method> roleMethods;
	private static final HashSet<Method> nonRoleMethods = new HashSet<Method>();
	static {
		nonRoleMethods.addAll(asList(Object.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(ObjWrapper.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Obj.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Stru.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Map.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Iterable.class.getDeclaredMethods()));
	}

	public WrapperInvocationHandler(Obj o, Class<?> role) {
		this.o = o != null ? o : new Obj();
		roleMethods = new HashSet<Method>();
		for (Method m : role.getMethods()) {
			if (!nonRoleMethods.contains(m)) {
				roleMethods.add(m);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		try {
			if ("unwrap".equals(name)) {
				return o;
			} else if (roleMethods.contains(method)) {
				Class<?> type = method.getReturnType();
				int argsL = (args == null ? 0 : args.length);
				if (argsL == 0) { // any getter
					if (Collection.class.isAssignableFrom(type)) { // collection getter
						Arr arr = o.getArr(name);
						if (arr != null) {
							Collection<Object> coll = type.isInterface()
								? new LinkedList<>()
								: (Collection<Object>) type.newInstance();
							Class<?> itemType = null;
							try {
								itemType = getCollectionReturnComponentType(method);
							} catch (Exception e) {
								// dynamic mode on
							}
							for (int i=0, l=arr.length(); i < l; i++) {
								Object v = arr.opt(i);
								if (itemType == null && v == null) {
									coll.add(null);
								} else {
									if (itemType == null) {
										itemType = v.getClass();
									}
									coll.add(fromObjValue(v, itemType));
								}
							}
							return coll;
						} else {
							return null;
						}
					} else if (Map.class.isAssignableFrom(type)) { // map getter
						Obj obj = o.getObj(name);
						if (obj != null) {
							if (type.isInterface()) {
								type = LinkedHashMap.class;
							}
							Map<String, Object> map = (Map<String, Object>) type.newInstance();
							Class<?> itemType = null;
							try {
								Pair<Class<?>, Class<?>> keyValTypes = getMapReturnComponentTypes(method);
								itemType = keyValTypes.second;
							} catch (Exception e) {
								// dynamic mode on
							}
							Iterator<String> i = obj.keys();
							while (i.hasNext()) {
								String key = i.next();
								Object val = obj.get(key);
								if (itemType == null && val == null) {
									map.put(key, null);
								} else {
									if(itemType == null) {
										itemType = val.getClass();
									}
									map.put(key, fromObjValue(val, itemType));
								}
							}
							return map;
						} else {
							return null;
						}
					} else { // simple getter
						return fromObjValue(o.get(name), type);
					}
				} else if (argsL == 1) { // setter
					o.put(name, toObjValue(args[0]));
					return proxy;
				} else { // bail out
					throw new RuntimeException();
				}
			} else {
				return method.invoke(o, args);
			}
		} catch (InvocationTargetException e) {
			throw Util.ifNull((Exception)e.getCause(), e);
		}
	}


	private Class<?> getCollectionReturnComponentType(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return cls.value();
		}
		Type t = method.getGenericReturnType();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)t;
			Type[] actualTypeArgs = pt.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 1) {
				Type at = actualTypeArgs[0];
				if (at instanceof Class<?>) {
					return (Class<?>) at;
				}
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}

	private Pair<Class<?>, Class<?>> getMapReturnComponentTypes(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return new Pair<Class<?>, Class<?>>(String.class, cls.value());
		}
		Type t = method.getGenericReturnType();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)t;
			Type[] actualTypeArgs = pt.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 2) {
				Type at0 = actualTypeArgs[0], at1 = actualTypeArgs[1];
				if (at0 instanceof Class<?> && at1 instanceof Class<?>) {
					return new Pair<Class<?>, Class<?>>((Class<?>)at0, (Class<?>)at1);
				}
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}


	private Object fromObjValue(Object v, Class<?> type) {
		if (null == v) {
			return null;
		} else if (Boolean.TYPE.equals(type)) { // expected boolean
			return Boolean.TRUE.equals((Boolean)v); // force true/false (also when v == null)
		} else if (JsonType.isNullablePrimitive(v)) {
			if (Double.class.equals(type) || double.class.equals(type)) {
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
			Obj o = (Obj) v;
			WrapType wrapType = type.getAnnotation(WrapType.class);
			if (wrapType != null) {
				Builder builder = createBuilder(wrapType, type);
				return builder.apply(o);
			} else if (WrapperItemFactory.class.isAssignableFrom(type)) {
				try {
					Method factoryMethod = findWrapMethod(type);
					return factoryMethod.invoke(null, v);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			} else if (ObjWrapper.class.isAssignableFrom(type)) { // sub object
				return WF.wrap(o, type);
			} else {
				throw new IllegalArgumentException("cannot convert value:"+v+" to type:"+type.getName());
			}
		} else {
			throw new IllegalArgumentException("cannot convert value:"+v+" to type:"+type.getName());
		}
	}

	private static final Map<Class<?>, Builder> BUILDERS_CACHE = new HashMap<>();
	private class Builder {
		private final String field;
		private final Map<String, Class<?>> typeMap = new HashMap<>();
		private Builder(WrapType wrapType, Class<?> type) {
			field = wrapType.field();
			String[] values = wrapType.values();
			Class<?>[] types = wrapType.types();
			for (int i = 0; i < values.length; i++) {
				typeMap.put(values[i], types[i]);
			}
		}
		private Object apply(Obj o) {
			return WF.wrap(o, typeMap.get(o.opt(field)));
		}
	}
	private Builder createBuilder(WrapType wrapType, Class<?> type) {
		Builder builder = BUILDERS_CACHE.get(type);
		if (builder == null) {
			builder = new Builder(wrapType, type);
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
					} catch (NoSuchMethodException e1) {

					}
				}
			}
			throw new NoSuchMethodException();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private Object toObjValue(Object v) throws JSONException {
		if (v instanceof Collection) {
			Arr arr = new Arr();
			for (Object o : (Collection<Object>)v) {
				arr.put(toObjValue(o));
			}
			return arr;
		} else if (v instanceof Map) {
			Obj obj = new Obj();
			for (Entry<String, Object> entry : ((Map<String, Object>)v).entrySet()) {
				obj.put(entry.getKey(), toObjValue(entry.getValue()));
			}
			return obj;
		} else if (v instanceof ObjWrapper) { // sub object
			return ((ObjWrapper)v).unwrap();
		}
		return v;
	}
}