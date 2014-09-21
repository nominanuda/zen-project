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
package org.hibernate.tuple.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.DynamicMapInstantiator;

public class DynaMapDynamicMapInstantiator extends DynamicMapInstantiator {
	private static final long serialVersionUID = 8117036205790668708L;

	public DynaMapDynamicMapInstantiator() {
	}
	public DynaMapDynamicMapInstantiator(PersistentClass mappingInfo) {
		super(mappingInfo);
	}

	protected Map generateMap() {
		return new InnerMap();
	}
	private static class InnerMap<K, V> extends LinkedHashMap<K, V> {
		private final Object oo = new Object();

		@Override
		public boolean equals(Object o) {
			return oo.equals(o);
		}

		@Override
		public int hashCode() {
			return oo.hashCode();
		}
	}
}
