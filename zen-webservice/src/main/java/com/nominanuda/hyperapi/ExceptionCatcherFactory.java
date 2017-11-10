package com.nominanuda.hyperapi;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.nominanuda.web.http.HttpAppException;

public class ExceptionCatcherFactory {
	protected IHttpAppExceptionRenderer exceptionRenderer = new HttpExceptionRenderer();
	
	public <T> T getInstance(final T apiImpl, Class<? extends T> apiInterface) {
		return apiInterface.cast(Proxy.newProxyInstance(apiInterface.getClassLoader(), new Class[] { apiInterface }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return method.invoke(apiImpl, args);
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();
					if (cause != null && cause instanceof HttpAppException) {
						return exceptionRenderer.statusAndRender((HttpAppException)cause, method.getReturnType()).get1();
					}
				}
				return null;
			}
		}));
	}
	
	
	/* setters */
	
	public void setExceptionRenderer(IHttpAppExceptionRenderer exceptionRenderer) {
		this.exceptionRenderer = exceptionRenderer;
	}
}