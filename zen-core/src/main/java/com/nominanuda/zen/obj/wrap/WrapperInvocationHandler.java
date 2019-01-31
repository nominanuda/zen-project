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

import static java.util.Arrays.asList;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JixSrc;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.obj.StruFactory;
import com.nominanuda.zen.obj.wrap.getter.IGetter;
import com.nominanuda.zen.stereotype.Copyable;
import com.nominanuda.zen.stereotype.Value;

class WrapperInvocationHandler implements InvocationHandler {
	
	private static final HashSet<Method> NON_ROLE_METHODS = new HashSet<>();
	static {
		NON_ROLE_METHODS.addAll(asList(Object.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(ObjWrapper.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(Obj.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(Stru.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(Map.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(Iterable.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(JixSrc.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(Value.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(Copyable.class.getDeclaredMethods()));
		NON_ROLE_METHODS.addAll(asList(StruFactory.class.getDeclaredMethods()));
	}

	private final Obj o;
	private final Set<Method> roleMethods;
	private final Set<Method> defaultMethods;
	private final IGetter[] getters;


	WrapperInvocationHandler(Obj o, Class<?> role, IGetter[] getters) {
		this.o = o != null ? o : Obj.make();
		roleMethods = new HashSet<Method>();
		defaultMethods = new HashSet<Method>();
		for (Method m : role.getMethods()) {
			if (m.isDefault()) {
				defaultMethods.add(m);
			} else if(NON_ROLE_METHODS.contains(m)) {
				// standard behaviour
			} else {
				roleMethods.add(m);
			}
		}
		this.getters = getters;
	}
	

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			String name = method.getName();
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
				switch (args == null ? 0 : args.length) {
				case 0: // getter
					for (IGetter getter : getters) {
						if (getter.supports(method)) {
							return getter.extract(o, method);
						}
					}
					throw new RuntimeException("don't have any getter for property " + name);
				case 1:
					o.put(name, toStruValue(args[0]));
					return proxy;
				}
				throw new RuntimeException("don't know how to manage ObjWrapper method " + name);
			} else if ("equals".equals(name) && args.length == 1) { // allows [Proxy].equals([Proxy])
				return args[0] == proxy;
			} else {
				return method.invoke(o, args);
			}
		} catch (InvocationTargetException e) {
			throw Check.ifNull((Exception)e.getCause(), e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private Object toStruValue(Object v) {
		if (v instanceof Collection) {
			Arr arr = Arr.make();
			for (Object o : (Collection<Object>)v) {
				arr.add(toStruValue(o));
			}
			return arr;
		} else if (v instanceof Map) {
			Obj obj = Obj.make();
			for (Entry<Object, Object> entry : ((Map<Object, Object>)v).entrySet()) {
				obj.store(entry.getKey().toString(), toStruValue(entry.getValue()));
			}
			return obj;
		} else if (v instanceof ObjWrapper) { // sub object
			return ((ObjWrapper)v).unwrap();
		} else if (v instanceof Enum) {
			return v.toString();
		}
		return v;
	}
}