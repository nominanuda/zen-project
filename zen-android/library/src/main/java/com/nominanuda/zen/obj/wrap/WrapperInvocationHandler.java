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

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
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
	private final JSONObject o;
	private final Class<?> role;
	private final Set<Method> roleMethods = new HashSet<Method>();
	private static final HashSet<Method> nonRoleMethods = new HashSet<Method>();

	static {
		nonRoleMethods.addAll(asList(Object.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(ObjWrapper.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Obj.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Stru.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Map.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Iterable.class.getDeclaredMethods()));
	}

	public WrapperInvocationHandler(JSONObject o, Class<?> role) {
		this.role = role;
		this.o = o != null ? o : new Obj();
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
			} else if ("as".equals(name)) {
				Class<?> enhancement = (Class<?>) args[1];
				InvocationHandler eh = new EnhancedInvocationHandler(enhancement, role, proxy);
				return Proxy.newProxyInstance(enhancement.getClassLoader(), new Class[] { (Class<?>) args[0] }, eh);
			} else if (roleMethods.contains(method)) {
				Class<?> type = method.getReturnType();
				int argsL = (args == null ? 0 : args.length);
				if (argsL == 0) { // any getter
					if (Collection.class.isAssignableFrom(type)) { // collection getter
						JSONArray arr = o.optJSONArray(name);
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
							for (int i = 0, l = arr.length(); i < l; i++) {
								Object val = arr.isNull(i) ? null : arr.opt(i); // as per issue https://issuetracker.google.com/issues/36924550
								if (itemType == null && val == null) {
									coll.add(null);
								} else {
									if (itemType == null) {
										itemType = val.getClass();
									}
									coll.add(fromObjValue(val, itemType));
								}
							}
							return coll;
						} else {
							return null;
						}
					} else if (Map.class.isAssignableFrom(type)) { // map getter
						JSONObject obj = o.optJSONObject(name);
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
								Object val = obj.isNull(key) ? null : obj.opt(key); // as per issue https://issuetracker.google.com/issues/36924550
								if (itemType == null && val == null) {
									map.put(key, null);
								} else {
									if (itemType == null) {
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
						return fromObjValue(o.isNull(name) ? null : o.opt(name), type); // as per issue https://issuetracker.google.com/issues/36924550
					}
				} else if (argsL == 1) { // setter
					o.put(name, toObjValue(args[0]));
					return proxy;
				} else { // bail out
					throw new RuntimeException();
				}
			} else if ("equals".equals(name) && args.length == 1) { // allows [Proxy].equals([Proxy])
				return args[0] == proxy;
			} else {
				return method.invoke(o, args);
			}
		} catch (InvocationTargetException e) {
			throw Check.ifNull((Exception) e.getCause(), e);
		}
	}


	private Class<?> getCollectionReturnComponentType(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return cls.value();
		}
		Type t = method.getGenericReturnType();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			Type[] actualTypeArgs = pt.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 1) {
				try {
					String name = actualTypeArgs[0].toString();
					return Class.forName(name.substring(name.indexOf(' ') + 1)); // remove "interface " in front of name
				} catch (ClassNotFoundException e) {
					throw e;
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
			ParameterizedType pt = (ParameterizedType) t;
			Type[] actualTypeArgs = pt.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 2) {
				try {
					String name0 = actualTypeArgs[0].toString();
					String name1 = actualTypeArgs[1].toString();
					return new Pair<>(
							Class.forName(name0.substring(name0.indexOf(' ') + 1)), // remove "interface " in front of name
							Class.forName(name1.substring(name1.indexOf(' ') + 1)) // remove "interface " in front of name
					);
				} catch (ClassNotFoundException e) {
					throw e;
				}
			}
		}
		throw new ClassNotFoundException("could not determine generic return type for method " + method.toString());
	}


	private Object fromObjValue(Object v, Class<?> type) {
		if (null == v) {
			return null;
		} else if (Boolean.TYPE.equals(type)) { // expected boolean
			return Boolean.TRUE.equals((Boolean) v); // force true/false (also when v == null)
		} else if (JsonType.isNullablePrimitive(v)) {
			if (Double.class.equals(type) || double.class.equals(type)) {
				return ((Number) v).doubleValue();
			} else if (Float.class.equals(type) || float.class.equals(type)) {
				return ((Number) v).floatValue();
			} else if (Integer.class.equals(type) || int.class.equals(type)) {
				return ((Number) v).intValue();
			} else if (Long.class.equals(type) || long.class.equals(type)) {
				return ((Number) v).longValue();
			} else {
				return v;
			}
		} else if (JsonType.isJSONObject(v)) {
			JSONObject o = (JSONObject) v;
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
				return WF.wrap(o, type);
			} else if (Obj.class.isAssignableFrom(type)) { // obj
				return Obj.make(o);
			} else {
				throw new IllegalArgumentException("cannot convert value:" + v + " to type:" + type.getName());
			}
		} else {
			throw new IllegalArgumentException("cannot convert value:" + v + " to type:" + type.getName());
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

		private Object apply(JSONObject o) {
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
			for (Object o : (Collection<Object>) v) {
				arr.put(toObjValue(o));
			}
			return arr;
		} else if (v instanceof Map) {
			Obj obj = new Obj();
			for (Entry<String, Object> entry : ((Map<String, Object>) v).entrySet()) {
				obj.put(entry.getKey(), toObjValue(entry.getValue()));
			}
			return obj;
		} else if (v instanceof ObjWrapper) { // sub object
			return ((ObjWrapper) v).unwrap();
		}
		return v;
	}
}