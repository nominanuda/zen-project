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

import static com.nominanuda.zen.obj.wrap.Wrap.WF;
import static java.util.Arrays.asList;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JixSrc;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.obj.StruFactory;
import com.nominanuda.zen.stereotype.Copyable;
import com.nominanuda.zen.stereotype.Value;

class WrapperInvocationHandler implements InvocationHandler {
	private final Obj o;
	private final Set<Method> roleMethods;
	private final Set<Method> defaultMethods;
	private static final HashSet<Method> nonRoleMethods = new HashSet<Method>();
	static {
		nonRoleMethods.addAll(asList(Object.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(ObjWrapper.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Obj.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Stru.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Map.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Iterable.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(JixSrc.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Value.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Copyable.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(StruFactory.class.getDeclaredMethods()));
	}

	public WrapperInvocationHandler(Obj o, Class<?> role) {
		this.o = o != null ? o : Obj.make();
		roleMethods = new HashSet<Method>();
		defaultMethods = new HashSet<Method>();
		for (Method m : role.getMethods()) {
			if (m.isDefault()) {
				defaultMethods.add(m);
			} else if(nonRoleMethods.contains(m)) {
				// standard behaviour
			} else {
				roleMethods.add(m);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		try {
			if (defaultMethods.contains(method)) {
				Field f = Lookup.class.getDeclaredField("IMPL_LOOKUP");
				f.setAccessible(true);
				Lookup lookup = (Lookup)f.get(null);
				final Object result = lookup
					.unreflectSpecial(method, method.getDeclaringClass())
					.bindTo(proxy)
					.invokeWithArguments(args);
				return result;
			} else if ("unwrap".equals(name)) {
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
							} catch(Exception e) {
								// dynamic mode on
							}
							for (Object v : arr) {
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
								Tuple2<Class<?>, Class<?>> keyValTypes = getMapReturnComponentTypes(method);
								itemType = keyValTypes.get1();
							} catch(Exception e) {
								// dynamic mode on
							}
							for (String key : obj.keySet()) {
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
				} else if (argsL == 1) {//setter
					o.put(name, toDataObjectValue(args[0]));
					return proxy;
				} else { // bail out
					throw new RuntimeException();
				}
			} else if ("equals".equals(name) && args.length == 1) { // allows [Proxy].equals([Proxy])
				return args[0] == proxy;
			} else {
				return method.invoke(o, args);
			}
		} catch(InvocationTargetException e) {
			throw Check.ifNull((Exception)e.getCause(), e);
		}
	}

	private Class<?> getCollectionReturnComponentType(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return cls.value();
		}
		Type t1 = method.getGenericReturnType();
		if (t1 instanceof ParameterizedType) {
			ParameterizedType t2 = (ParameterizedType)t1;
			Type[] actualTypeArgs = t2.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 1) {
				try {
					return Class.forName(actualTypeArgs[0].getTypeName());
				} catch (ClassNotFoundException e) {
					throw e;
				}
			}
		}
		throw new ClassNotFoundException(
			"could not determine generic return type for method "+method.toString());
	}

	private Tuple2<Class<?>, Class<?>> getMapReturnComponentTypes(Method method) throws ClassNotFoundException {
		Cls cls = method.getAnnotation(Cls.class);
		if (cls != null) {
			return new Tuple2<>(String.class, cls.value());
		}
		Type t1 = method.getGenericReturnType();
		if (t1 instanceof ParameterizedType) {
			ParameterizedType t2 = (ParameterizedType)t1;
			Type[] actualTypeArgs = t2.getActualTypeArguments();
			if (actualTypeArgs != null && actualTypeArgs.length == 2) {
				try {
					return new Tuple2<>(
						Class.forName(actualTypeArgs[0].getTypeName()),
						Class.forName(actualTypeArgs[1].getTypeName()));
				} catch (ClassNotFoundException e) {
					throw e;
				}
			}
		}
		throw new ClassNotFoundException(
			"could not determine generic return type for method "+method.toString());
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
			Obj o = (Obj)v;
			WrapType wrapType = type.getAnnotation(WrapType.class);
			if (wrapType != null) {
				Function<Obj, Object> builder = createBuilder(wrapType, type);
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

	private static final Map<Class<?>, Function<Obj, Object>> BUILDER_CACHE = new HashMap<>();
	private Function<Obj, Object> createBuilder(WrapType wrapType, Class<?> type) {
		Function<Obj, Object> builder = BUILDER_CACHE.get(type);
		if (builder == null) {
			String field = wrapType.field();
			Map<String, Class<?>> typeMap = new HashMap<>();
			String[] values = wrapType.values();
			Class<?>[] types = wrapType.types();
			for (int i = 0; i < values.length; i++) {
				typeMap.put(values[i], types[i]);
				builder = (o) -> WF.wrap(o, typeMap.get(o.fetch(field)));
				BUILDER_CACHE.put(type, builder);
			}
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
	
	
	@SuppressWarnings("unchecked")
	private Object toDataObjectValue(Object v) {
		if (v instanceof Collection) {
			Arr arr = Arr.make();
			for (Object o : (Collection<Object>)v) {
				arr.add(toDataObjectValue(o));
			}
			return arr;
		} else if (v instanceof Map) {
			Obj obj = Obj.make();
			for (Entry<String, Object> entry : ((Map<String, Object>)v).entrySet()) {
				obj.store(entry.getKey(), toDataObjectValue(entry.getValue()));
			}
			return obj;
		} else if (v instanceof ObjWrapper) { // sub object
			return ((ObjWrapper)v).unwrap();
		}
		return v;
	}
}