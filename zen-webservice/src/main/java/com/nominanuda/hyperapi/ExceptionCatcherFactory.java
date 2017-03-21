package com.nominanuda.hyperapi;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http401Exception;
import com.nominanuda.web.http.Http403Exception;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http4xxException;
import com.nominanuda.web.http.Http5xxException;
import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Obj;

public class ExceptionCatcherFactory {
	protected IHttpAppExceptionRenderer exceptionRenderer = new IHttpAppExceptionRenderer() {
		@Override
		public Tuple2<Integer, Object> statusAndRender(HttpAppException e, Class<?> returnType) {
			Object result = returnType.isAssignableFrom(Obj.class) ? Obj.make() : new Object();
			return new Tuple2<Integer, Object>(e.getStatusCode(), result);
		}
		
		@Override
		public void parseAndThrow(int status, Object response) throws HttpAppException {
			if (status >= 400) {
				if (status < 500) {
					switch (status) {
					case 400:
						throw new Http400Exception(response.toString());
					case 401:
						throw new Http401Exception(response.toString());
					case 403:
						throw new Http403Exception(response.toString());
					case 404:
						throw new Http404Exception(response.toString());
					default:
						throw new Http4xxException(response.toString(), status);
					}
				}
				throw new Http5xxException(response.toString(), status);
			}
		}
	};

	
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