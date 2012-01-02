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
package com.nominanuda.lang;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.nominanuda.code.Immutable;
import com.nominanuda.code.ThreadSafe;

@Immutable @ThreadSafe
public class MaterializedFuture<T> implements Future<T> {
	private final T obj;
	private final Exception e;

	public MaterializedFuture(T _obj) {
		obj = _obj;
		e = null;
	}
	public MaterializedFuture(Exception _e) {
		obj = null;
		e = _e;
	}
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}
	public T get() throws InterruptedException, ExecutionException {
		if(e != null) {
			throw new ExecutionException(e);
		}
		return obj;
	}
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return get();
	}
	public boolean isCancelled() {
		return false;
	}
	public boolean isDone() {
		return true;
	}
}
