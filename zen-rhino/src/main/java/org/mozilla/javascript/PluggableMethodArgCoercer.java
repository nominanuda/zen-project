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
package org.mozilla.javascript;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.nominanuda.rhino.ObjectCoercer;
import com.nominanuda.zen.common.Tuple2;

public class PluggableMethodArgCoercer implements MethodArgCoercer {
	private final Map<Class<?>, Tuple2<ObjectCoercer<Object, Object, Exception>, Integer>> convertors = new HashMap<>();

	public PluggableMethodArgCoercer() {}
	public PluggableMethodArgCoercer(Map<Class<?>, Tuple2<ObjectCoercer<Object, Object, Exception>, Integer>> convertors) {
		setConvertors(convertors);
	}
	
	public int getConversionWeight(Object valueFrom, Class<?> typeTo) {
		if (Enum.class.isAssignableFrom(typeTo)) { // enum is a special (more generic) case
			return 1;
		}
		Tuple2<ObjectCoercer<Object, Object, Exception>,Integer> t = findConvertor(typeTo, valueFrom);
		return t == null ? Integer.MIN_VALUE : t.get1();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object coerceTypeImpl(Class<?> typeTo, Object valueFrom) {
		if (Enum.class.isAssignableFrom(typeTo)) { // enum is a special (more generic) case
			return valueFrom != null
				? Enum.valueOf((Class<Enum>) typeTo, valueFrom.toString())
				: null;
		}
		Tuple2<ObjectCoercer<Object, Object, Exception>,Integer> t = findConvertor(typeTo, valueFrom);
		if (t != null) {
			try {
				return t.get0().apply(valueFrom);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return REGULAR_COERCE_OP;
		}
	}

	protected @Nullable Tuple2<ObjectCoercer<Object, Object, Exception>, Integer> findConvertor(Class<?> typeTo, Object valueFrom) {
		for (Entry<Class<?>, Tuple2<ObjectCoercer<Object, Object, Exception>,Integer>> e : convertors.entrySet()) {
			if (e.getKey().equals(typeTo) && e.getValue().get0().canConvert(valueFrom)) {
				return e.getValue();
			}
		}
		return null;
	}
	
	
	/* setters */

	public void setConvertors(Map<Class<?>, Tuple2<ObjectCoercer<Object, Object, Exception>, Integer>> convertors) {
		this.convertors.clear();
		this.convertors.putAll(convertors);
	}
}
