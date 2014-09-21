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
package com.nominanuda.dataview;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Collections;

public class BeanPropertyReader implements PropertyReader<Object> {
	private Set<String> skipProps = Collections.hashSet("class", "annotation");
	//TODO private WeakHashMap<Object, BeanInfo> cache = new WeakHashMap<Object, BeanInfo>();

	public Collection<String> readableProps(Object m) {
		LinkedList<String> res = new LinkedList<String>();
		for(PropertyDescriptor pd : info(m).getPropertyDescriptors()) {
			Method getter = pd.getReadMethod();
			String prop = pd.getName();
			if(getter != null && ! skipProps.contains(prop)) {
				res.add(prop);
			}
		}
		return res;
	}
	public Object read(Object m, String k) {
		Method getter = Check.illegalargument.assertNotNull(
				findReadMethod(m, k), "property not found "+k);
		try {
			return getter.invoke(m);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public boolean accepts(Object o) {
		return true;
	}
	public boolean hasProp(Object o, String k) {
		return findReadMethod(o, k) != null;
	}
	private BeanInfo info(Object o) {
		try {
			BeanInfo info = Introspector.getBeanInfo(o.getClass());
			return info;
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private @Nullable Method findReadMethod(Object o, String k) {
		for(PropertyDescriptor pd : info(o).getPropertyDescriptors()) {
			if(k.equals(pd.getName())) {
				return pd.getReadMethod();
			}
		}
		return null;
	}
}