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
package com.nominanuda.zen.stereotype;

import java.util.HashMap;
import java.util.Map;

public class ScopedSingletonFactory {
	private static ScopedSingletonFactory INSTANCE;

	public synchronized static ScopedSingletonFactory getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ScopedSingletonFactory();
		}
		return INSTANCE;
	}

	private static final Object JVM_SCOPE = new Object();
	private Map<Object, Map<Class<?>, Object>> scopes = new HashMap<>();

	public <V> V buildJvmSingleton(Class<V> clazz) throws IllegalArgumentException {
		return buildScopedSingleton(JVM_SCOPE, clazz);
	}

	@SuppressWarnings("unchecked")
	public <V> V buildScopedSingleton(Object appScope, Class<V> clazz) {
		try {
			Map<Class<?>, Object> scope = scopes.get(appScope);
			if(scope == null) {
				scope = new HashMap<>();
				scopes.put(appScope, scope);
			}
			Object o = scope.get(clazz);
			if(o != null) {
				return (V)o;
			} else {
				V v = clazz.newInstance();
				scope.put(clazz, v);
				return v;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
