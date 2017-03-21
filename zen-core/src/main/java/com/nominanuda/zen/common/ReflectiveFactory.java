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
package com.nominanuda.zen.common;

import static com.nominanuda.zen.common.Check.illegalstate;

import com.nominanuda.zen.stereotype.Factory;


public class ReflectiveFactory<T> implements Factory<T> {
	private Class<? extends T> clazz;

	public ReflectiveFactory(Class<? extends T> clazz) {
		this.clazz = clazz;
	}

	public ReflectiveFactory() {
	}

	public void setClazz(Class<? extends T> clazz) {
		illegalstate.assertNull(this.clazz, "class already set");
		this.clazz = clazz;
	}

	public T get() {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}