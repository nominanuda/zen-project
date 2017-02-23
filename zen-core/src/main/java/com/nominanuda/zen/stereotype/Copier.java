package com.nominanuda.zen.stereotype;

import static com.nominanuda.zen.common.Ex.EX;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.nominanuda.zen.common.Check;


public interface Copier<T> {
	/**
	 * @param t
	 * @return the copy x such as that x.equals(t); if the object is immutable
	 * than can return t itself so that x == t. If t is <code>null</code>
	 * the method returns <code>null</code>
	 */
	T copy(@Nullable T t);

	/**
	 * @return the basic {@link Copier} that can copy {@link Copyable}s and returns the
	 * supplied parameter if it is a basic java immutable type or carries the {@link Immutable}
	 * annotation. Also {@link Cloneable}s are supported if they expose the {@link Object#clone()} method
	 * as public
	 */
	static <X> Copier<X> basic() {
		return new Copier<X>() {
			@Override
			public X copy(X x) throws IllegalArgumentException {
				if(x == null) {
					return null;
				} else if(x instanceof Copyable) {
					return ((Copyable)x).copyCast();
				}
				Class<?> clazz = x.getClass();
				if(clazz.isAnnotationPresent(Immutable.class)) {
					return x;
				} else if(Check.isInstanceOf(x, 
					String.class, Number.class, Locale.class, UUID.class, URL.class, URI.class)) {
					return x;
				} else if(x instanceof Cloneable) {
					Method cloneMethod;
					try {
						cloneMethod = clazz.getMethod("clone", new Class[]{});
					} catch (NoSuchMethodException | SecurityException e1) {
						throw new IllegalArgumentException(
							"don't know how to copy an object of type "+ x.getClass().getName());
					}
					if(cloneMethod != null) {
						try {
							@SuppressWarnings("unchecked")
							X res = (X)cloneMethod.invoke(x);
							return res;
						} catch (IllegalAccessException
								| InvocationTargetException e) {
							throw EX.uncheckedExecution(e);
						}
					} else {//defensive programming <:P
						throw new IllegalArgumentException(
							"don't know how to copy an object of type "+ x.getClass().getName());
					}
				} else {
					throw new IllegalArgumentException(
						"don't know how to copy an object of type "+ x.getClass().getName());
				}
			}
		};
	}

	/**
	 * 
	 * @return a zero copier that always returns the supplier parameter
	 */
	static <X> Copier<X> unsafeNoOp() {
		return new Copier<X>() {
			@Override
			public X copy(X t) {
				return t;
			}
		};
	}
}
