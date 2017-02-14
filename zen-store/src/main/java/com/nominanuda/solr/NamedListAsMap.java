package com.nominanuda.solr;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.solr.common.util.NamedList;

public class NamedListAsMap<T> extends AbstractMap<String,T> {
	private final NamedList<T> nl;

	public NamedListAsMap(NamedList<T> nl) {
		super();
		this.nl = nl;
	}

	@Override
	public Set<java.util.Map.Entry<String, T>> entrySet() {
		return new NamedListAsEntrySet<T>(nl);
	}

	public static class NamedListAsEntrySet<V> extends AbstractSet<Entry<String, V>> {
		private final NamedList<V> nl;

		public NamedListAsEntrySet(NamedList<V> nl) {
			super();
			this.nl = nl;
		}

		@Override
		public Iterator<Entry<String, V>> iterator() {
			final Iterator<Entry<String, V>> i = nl.iterator();
			return new Iterator<Entry<String, V>>() {
				@Override public boolean hasNext() {
					return i.hasNext();
				}
				@Override public Entry<String, V> next() {
					final Entry<String, V> e = i.next();
					if(e == null) {
						return null;
					} else if(e.getValue() instanceof NamedList) {
						return new Entry<String, V>() {
							@Override public String getKey() {
								return e.getKey();
							}
							@SuppressWarnings({ "unchecked", "rawtypes" })
							@Override public V getValue() {
								return (V)new NamedListAsMap((NamedList<?>)e.getValue());
							}
							@Override public V setValue(V value) {
								throw new UnsupportedOperationException("Entry#setValue");
							}
						};
					} else {
						return e;
					}
				}
				@Override public void remove() {
					throw new UnsupportedOperationException();
				}
			};
//			return nl.iterator();
		}

		@Override
		public int size() {
			return nl.size();
		}

	}
}
