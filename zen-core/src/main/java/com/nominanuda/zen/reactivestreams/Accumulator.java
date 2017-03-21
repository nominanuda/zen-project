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
package com.nominanuda.zen.reactivestreams;

import static com.nominanuda.zen.common.Check.notNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.nominanuda.zen.stereotype.UncheckedExecutionException;

/**
 * subclasses must implement {@link Subscriber#onNext(Object)} and {@link Subscriber#onComplete()}
 * the latter must call the protected final {@link Accumulator#setResult(Object)}
 * @param <T>
 * @param <R>
 */
public abstract class Accumulator<T,R> implements Subscriber<T>, Supplier<R> {
	private AtomicBoolean completed = new AtomicBoolean(false);
	private R result;
	private Throwable exc;
	
	@Override
	public final void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public final void onError(Throwable t) {
		completed.set(true);
		exc = notNull(t);
	}

	@Override
	public final @Nullable R get() {
		if(exc != null) {
			throw new UncheckedExecutionException(exc);
		} else if(completed.get()) {
			return result;
		} else {
			throw new IllegalStateException("result not set");
		}
	}

	protected final void setResult(R res) {
		if(completed.compareAndSet(false, true)) {
			result = res;
		}
	}
}
