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

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.ObjectConvertor;
import com.nominanuda.lang.Tuple2;

public class PluggableMethodArgCoercer implements MethodArgCoercer {
	private final Map<Class<?>, Tuple2<ObjectConvertor<Object, Object, Exception>,Integer>> convertors = new HashMap<Class<?>, Tuple2<ObjectConvertor<Object,Object,Exception>,Integer>>();

	public PluggableMethodArgCoercer() {}
	public PluggableMethodArgCoercer(Map<Class<?>, Tuple2<ObjectConvertor<Object, Object, Exception>, Integer>> convertors) {
		setConvertors(convertors);
	}
	
	public int getConversionWeight(Object value, Class<?> type) {
		Tuple2<ObjectConvertor<Object, Object, Exception>,Integer> t = findConvertor(type, value);
		return t == null ? Integer.MIN_VALUE : t.get1();
	}

	private @Nullable Tuple2<ObjectConvertor<Object, Object, Exception>, Integer> findConvertor(Class<?> type, Object value) {
		for (Entry<Class<?>, Tuple2<ObjectConvertor<Object, Object, Exception>,Integer>> e : convertors.entrySet()) {
			if (e.getKey().equals(type) && e.getValue().get0().canConvert(value)) {
				return e.getValue();
			}
		}
		return null;
	}

	public Object coerceTypeImpl(Class<?> type, Object value) {
		Tuple2<ObjectConvertor<Object, Object, Exception>,Integer> t = findConvertor(type, value);
		if (t != null) {
			try {
				return t.get0().apply(value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return REGULAR_COERCE_OP;
		}
	}

	public void setConvertors(Map<Class<?>, Tuple2<ObjectConvertor<Object, Object, Exception>, Integer>> convertors) {
		this.convertors.clear();
		this.convertors.putAll(convertors);
	}

}
