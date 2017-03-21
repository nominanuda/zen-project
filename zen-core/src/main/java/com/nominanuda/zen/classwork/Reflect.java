/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.classwork;

import static com.nominanuda.zen.common.Str.STR;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

@ThreadSafe
public class Reflect {
	public static final Reflect REFL = ScopedSingletonFactory.getInstance().buildJvmSingleton(Reflect.class);

	public void setFieldValue(String name, Object value, Object target, boolean forceAccessibility) throws IllegalArgumentException {
		Field f = getField(name, target, forceAccessibility);
		try {
			f.set(target, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public void setFieldValueIncludingAncestors(String name, Object value, Object target, boolean forceAccessibility) throws IllegalArgumentException {
		Field f = findFieldIncludingAncestors(name, target, forceAccessibility);
		try {
			f.set(target, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public void setStaticFieldValue(String name, Object value, Class<?> cl, boolean forceAccessibility) throws IllegalArgumentException {
		Field f = findFieldOfClass(name, cl, true, forceAccessibility);
		try {
			f.set(null, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public Object getFieldValue(String name, Object target, boolean forceAccessibility) throws IllegalArgumentException {
		Field f = getField(name, target, forceAccessibility);
		try {
			return f.get(target);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public Object getFieldValueIncludingAncestors(String name, Object target, boolean forceAccessibility) throws IllegalArgumentException {
		Field f = findFieldIncludingAncestors(name, target, forceAccessibility);
		try {
			return f.get(target);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	public Object getStaticFieldValue(String name, Class<?> cl, boolean forceAccessibility) throws IllegalArgumentException {
		Field f = findFieldOfClass(name, cl, true, forceAccessibility);
		try {
			return f.get(null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	private Field getField(String name, Object obj, boolean forceAccessibility) {
		return findFieldOfClass(name, obj.getClass(), false, forceAccessibility);
	}
	private @Nullable Field findFieldIncludingAncestors(String name, Object obj, boolean forceAccessibility) {
		Class<?> clazz = obj.getClass();
		Field field = null;
		while((field = findFieldOfClass(name, clazz, false, forceAccessibility)) == null) {
			clazz = clazz.getSuperclass();
			if(clazz == null) {
				break;
			}
		}
		return field;
	}

	private @Nullable Field findFieldOfClass(String name, Class<?> cl, boolean isStatic, boolean forceAccessibility) throws IllegalArgumentException {
		Check.notNull(name);
		Check.notNull(cl);
		Field foundField = null;
		for(Field f : cl.getDeclaredFields()) {
			if (f.getName().equals(name)) {
				foundField = f;
				break;
			}
		}
		if(foundField == null) {
			return null;
		} else if(isStatic != Modifier.isStatic(foundField.getModifiers())) {
			throw new IllegalArgumentException("field "+name+" is "+(isStatic ? "not" : "")+" static");
		} else {
			if(forceAccessibility) {
				coerceFieldAccessibility(foundField);
			}
			return foundField;
		}
	}

	private void coerceFieldAccessibility(Field f) {
		if(! f.isAccessible()) {
			f.setAccessible(true);
		}
	}

	public Object invokeMethod(Object target, Method method, Object[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException  {
			return method.invoke(target, args);
	}
	
	public Object invokeMethod(Object target, String methodName, Object[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method m = findObjectMethod(target, methodName, args);
		Check.illegalargument.assertNotNull(m);
		return invokeMethod(target, m, args);
	}

	private @Nullable Method findObjectMethod(Object target, String methodName, Object[] params) {
		Class<?>[] paramTypes = new Class[params.length];
		for(int i = 0; i < params.length; i++) {
			paramTypes[i] = (params[i] != null) ? params[i].getClass() : AnyClass.class;
		}
		return findClassMethod(target.getClass(), methodName, paramTypes);
	}
		
	public @Nullable Method findClassMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		Check.notNull(clazz);
		Check.notNull(methodName);
		Method candidate = null;
		while (clazz != null && !Object.class.equals(clazz)) {
			Method[] methods = (clazz.isInterface() ? clazz.getMethods() : clazz.getDeclaredMethods());
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if (methodName.equals(method.getName()) && classArrayIsCompatible(paramTypes, method.getParameterTypes())) {
					if(candidate != null) {
						Check.illegalargument.fail(
							"ambiguous methods found named "+methodName
							+" on class "+clazz
							+" with arguments types :" + Arrays.toString(paramTypes)
							+" confilcting methods signature are "+Arrays.toString(candidate.getParameterTypes())+
							" and "+Arrays.toString(method.getParameterTypes()));
					}
					candidate = method;
				}
			}
			clazz = clazz.getSuperclass();
		}
		return candidate;
	}

	@SuppressWarnings("unchecked")
	public @Nullable <T> Constructor<T> findConstructor(Class<T> type, Class<?>[] paramTypes) throws IllegalArgumentException /*on ctor not found*/{
		Constructor<T>[] constructors = (Constructor<T>[])type.getConstructors();
		for(Constructor<T> c : constructors) {
			Class<?>[] ctorTypes = c.getParameterTypes();
			if(classArrayIsCompatible(ctorTypes, paramTypes)) {
				return (Constructor<T>)c;
			}
		}
		throw new IllegalArgumentException("constructor not found on class "+
			type.getName()+" for arguments ("+STR.join(", ", paramTypes)+")");
	}

	private boolean classArrayIsCompatible(Class<?>[] paramTypes, Class<?>[] declared) {
		if (paramTypes.length != declared.length) {
			return false;
		}
		for (int i = 0; i < declared.length; i++) {
			if (!classIsCompatible(paramTypes[i], declared[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean classIsCompatible(Class<?> paramType, Class<?> declared) {
		return AnyClass.class.equals(paramType) || declared.isAssignableFrom(paramType);
	}

	public static abstract class AnyClass {
		public String toString() { return "Anyclass";}
	}

	public boolean safeInstanceOf(Object o, String clazz) throws NullPointerException /*if o == null*/{
		try {
			Class<?> cl = Class.forName(clazz);
			return cl.isAssignableFrom(Check.notNull(o).getClass());
		} catch(ClassNotFoundException e) {
			return false;
		}
	}

	public boolean isInstanceOf(Object o, Class<?> clazz) throws NullPointerException /*if o == null*/{
		return clazz.isAssignableFrom(Check.notNull(o).getClass());
	}
}
