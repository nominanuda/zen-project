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
package com.nominanuda.dataobject;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static com.nominanuda.dataobject.WrappingFactory.WF;
import static java.util.Arrays.asList;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nominanuda.lang.Check;

public class WrapperInvocationHandler implements InvocationHandler {
	private final DataObject o;
	//private final Class<?> role;
	private final Set<Method> roleMethods;
	private final Set<Method> defaultMethods;
	private static final HashSet<Method> nonRoleMethods = new HashSet<Method>();
	static {
		nonRoleMethods.addAll(asList(Object.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(DataObjectWrapper.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(DataStruct.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(DataObject.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Iterable.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(PropertyBag.class.getDeclaredMethods()));
	}

	public WrapperInvocationHandler(DataObject o, Class<?> role) {
		this.o = o != null ? o : STRUCT.newObject();
		//this.role = role;
		roleMethods = new HashSet<Method>();
		defaultMethods = new HashSet<Method>();
		roleMethods.removeAll(nonRoleMethods);
		for(Method m : role.getMethods()) {
			if(m.isDefault()) {
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
			if(defaultMethods.contains(method)) {
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
				if (argsL == 0) { // simple getter
					return fromDataObjectValue(o.get(name), type);
				} else if (argsL == 1) {
					if (Collection.class.isAssignableFrom(type)) { // collection getter
						DataArray arr = o.getArray(name);
						if (arr != null) {
							Class<?> itemType = (Class<?>) args[0];
							Collection<Object> coll = type.isInterface()
								? new LinkedList<>()
								: (Collection<Object>) type.newInstance();
							for (Object v : STRUCT.castAsIterable(arr)) {
								coll.add(fromDataObjectValue(v, itemType));
							}
							return coll;
						}
						return null;
					} else if (Map.class.isAssignableFrom(type)) { // map getter
						DataObject obj = o.getObject(name);
						if (obj != null) {
							Class<?> itemType = (Class<?>) args[0];
							Map<String, Object> map = (Map<String, Object>) type.newInstance();
							for (String key : obj.getKeys()) {
								map.put(key, fromDataObjectValue(obj.get(key), itemType));
							}
							return map;
						}
						return null;
					} else { // setter
						o.put(name, toDataObjectValue(args[0]));
						return proxy;
					}
				} else { // bail out
					throw new RuntimeException();
				}
			} else {
				return method.invoke(o, args);
			}
		} catch(InvocationTargetException e) {
			throw Check.ifNull((Exception)e.getCause(), e);
		}
	}
	
	
	private Object fromDataObjectValue(Object v, Class<?> type) {
		if (Boolean.TYPE.equals(type)) { // expected boolean
			return Boolean.TRUE.equals(v); // force true/false (also when v == null)
		}
		if (v != null) {
			if (DataObjectWrapper.class.isAssignableFrom(type)) { // sub object
				return WF.wrap((DataObject)v, type);
			}
		}
		return v;
	}
	
	
	@SuppressWarnings("unchecked")
	private Object toDataObjectValue(Object v) {
		if (v != null) {
			if (v instanceof Collection) {
				DataArray arr = STRUCT.newArray();
				for (Object o : (Collection<Object>)v) {
					arr.add(toDataObjectValue(o));
				}
				return arr;
			}
			if (v instanceof Map) {
				DataObject obj = STRUCT.newObject();
				for (Entry<String, Object> entry : ((Map<String, Object>)v).entrySet()) {
					obj.put(entry.getKey(), entry.getValue());
				}
				return obj;
			}
			if (v instanceof DataObjectWrapper) { // sub object
				return ((DataObjectWrapper)v).unwrap();
			}
		}
		return v;
	}
}