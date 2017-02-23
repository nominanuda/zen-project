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

import java.util.function.Function;

import org.reactivestreams.Processor;

/**
 * base class to encapsulate the data transforming concern of 
 * {@link Processor}s. The two protected methods are supposed
 * to overridden. Following the open-close principle this class
 * and subclasses are rigid on this point. Most methods are declared
 * final. The transformation can be blocking and throw {@link Exception}s
 *
 * @param <T>
 * @param <R>
 */
public abstract class TransformingProcessor<T, R> implements Processor<T, R> {
	private Function<T, R> fn;

	/**
	 * extension point
	 * @see TransformingProcessor#applyTransformation(Object)
	 * @return the {@link Function} to apply
	 */
	protected Function<T, R> getFunction() {
		return fn;
	}

//	/**
//	 * extension point. The base implementation applies the {@link Function}
//	 * obtained by calling {@link TransformingProcessor#getFunction()}
//	 * @param t the {@link Processor#onNext(Object)} argument. 
//	 * @return the value to submit to {@link Processor}'s {@link Subscriber}
//	 * @throws Exception
//	 */
//	protected R applyTransformation(T t) throws Exception {
//		 return getFunction().apply(t);
//	}

	protected void in(T t) throws Exception {
		R r = getFunction().apply(t);
		out(r);
	}

	protected abstract void out(R r);


	public final void setFunction(Function<T, R> fn) {
		this.fn = fn;
	}
}
