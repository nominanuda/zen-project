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
