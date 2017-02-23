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
package com.nominanuda.zen.concurrent;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.nominanuda.zen.stereotype.UncheckedExecutionException;

public class Exec {
	public static final Runnable NOOP = () -> {};

	public static void sleep(long millis) throws UncheckedExecutionException {
		if(millis < 1) {
			return;
		}
		try {
			Thread.sleep(millis);
		} catch(InterruptedException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public static <T> CompletableFuture<T> exe(ThrowingSupplier<T> supplier) {
		return exe(supplier, newSingleThreadExecutor());
	}

	public static <T> CompletableFuture<T> exe(ThrowingSupplier<T> supplier, Executor executor) {
		CompletableFuture<T> result = new CompletableFuture<>();
		executor.execute(() -> {
			try {
				result.complete(supplier.supply());
			} catch(Exception e) {
				result.completeExceptionally(e);
			}
		});
		return result;
	}

	public static CompletableFuture<Void> exe(ThrowingRunnable runnable) {
		return exe(runnable, newSingleThreadExecutor());
	}

	public static CompletableFuture<Void> exe(ThrowingRunnable runnable, Executor executor){
		CompletableFuture<Void> result = new CompletableFuture<>();
		executor.execute(() -> {
			try {
				runnable.go();
				result.complete(null);
			} catch(Exception e) {
				result.completeExceptionally(e);
			}
		});
		return result;
	}
}
