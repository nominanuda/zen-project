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

import java.util.Collection;
import java.util.Map;

import com.nominanuda.lang.Check;

public class MapPropertyReader implements PropertyReader<Map<String, ? extends Object>> {
	public Collection<String> readableProps(Map<String, ? extends Object> m) {
		return m.keySet();
	}
	public Object read(Map<String, ? extends Object> m, String k) {
		return m.get(k);
	}
	public boolean accepts(Object o) {
		return Check.notNull(o) instanceof Map<?,?>;
	}
	public boolean hasProp(Map<String, ? extends Object> o, String k) {
		return o.containsKey(k);
	}
}