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
package com.nominanuda.hyperapi.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractAsyncInvoker implements AsyncInvoker {
	private List<FutureTask<?>> futures = new LinkedList<FutureTask<?>>();

	@SuppressWarnings("unchecked")
	public <T,Z> Z async(final Z delegee, final SuccessCallback<T> ss, final ErrorCallback ee) {
		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(final Object proxy, final Method m, final Object[] args)
					throws Throwable {
				Callable<T> task = new Callable<T>() {
					public T call() throws Exception {
						try {
							T res = (T)m.invoke(delegee, args);
							invokeSuccessCb(res, ss);
							return res;
						} catch(InvocationTargetException e) {
							invokeErrorCb(ee, (Exception)e.getTargetException());
							return null;
						}
					}
				};
				FutureTask<T> fut = new FutureTask<T>(task);
				executeFutureTask(fut);
				futures.add(fut);
				return null;
			}
		};
		Object proxy = Proxy.newProxyInstance(
			getClass().getClassLoader(), delegee.getClass().getInterfaces(), handler);
		return (Z)proxy;
	}

	protected abstract void executeFutureTask(FutureTask<?> fut);
	protected <T> void invokeSuccessCb(T result, SuccessCallback<T> ss) {
		ss.apply(result, "textStatus", null);
	}
	protected void invokeErrorCb(ErrorCallback ee, Exception e) {
		ee.apply(null, "textStatus", e);
	}

	@Override
	public List<FutureTask<?>> getFutures() {
		return futures;
	}

	public void await(long timeout, TimeUnit unit) throws Exception {
		long start = System.currentTimeMillis();
		long millis = TimeUnit.MILLISECONDS.convert(timeout, unit);
		for(Future<?> f : futures) {
			if(System.currentTimeMillis() - start > millis) {
				throw new TimeoutException();
			}
			try {
				f.get(timeout, unit);
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} catch (TimeoutException e) {
				throw e;
			}
		}
	}

}
