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
package com.nominanuda.hibernate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.lang.Strings;

public class ObjectExpander {
	private String idField = "id";
	private List<String> fixedProps = Arrays.asList("title");
	private List<String> discardedKeys = new LinkedList<String>();
	private static final DataStructHelper struct = new DataStructHelper();
	private Map<String, PathMapFactory> expandedMaps = new HashMap<String, PathMapFactory>();
	private Map<String, PathMapFactory> nakedMaps = new HashMap<String, PathMapFactory>();

	public DataObject expand(Map<String, Object> obj, String type, boolean expand) {
		PathMap pm = createPathMap(expand, type);//TODO
		return compileObject(obj, pm);
	}

	protected PathMap createPathMap(boolean expand, String typeOrNull) {
		if(typeOrNull == null) {
			return PathMapFactory.defaultPathMap();
		} else {
			PathMapFactory f = expand
				? expandedMaps.get(typeOrNull)
				: nakedMaps.get(typeOrNull);
			return f == null ? PathMapFactory.defaultPathMap() : f.create();
		}
	}
	//TODO array of array not supported
	private DataObject compileObject(Map<String, ? extends Object> m, PathMap pathMap) {
		for(String k : discardedKeys) {
			m.remove(k);
		}
		DataObject res = new DataObjectImpl();
		for(Entry<String, ? extends Object> entry : m.entrySet()) {
			String k = entry.getKey();
			Object v = entry.getValue();
			if(v == null) {
				continue;
			} else if(v instanceof Collection<?>) {
				if(pathMap.isTraversable(k)) {
					DataArray arr = res.putNewArray(k);
					for(Object o : (Collection<?>)v) {
						Object x = evalScalarOrObject(k, o, pathMap);
						arr.add(x);
					}
				}
			} else {
				Object x = evalScalarOrObject(k, v, pathMap);
				if(x != null) {
					res.put(k, x);
				}
			}
		}
		return res;
	}

	private Object evalScalarOrObject(String k, Object v, PathMap pathMap) {
		if(v == null) {
			return null;
		} else if(struct.isPrimitiveOrNull(v)) {
			return v;
		} else if(v instanceof Map<?,?>) {
			Map<String, Object> m = (Map<String, Object>)v;
			if(pathMap.isTraversable(k)) {
				return compileObject(m, pathMap.traverse(k));
			} else if(m.containsKey(idField)) {
				DataObjectImpl o = new DataObjectImpl();
				o.put(idField, (String)m.get(idField));
				for(String p : fixedProps) {
					if(m.containsKey(p)) {
						o.put(p, m.get(p));
					}
				}
				return o;
			} else {
				return compileObject(m, pathMap.traverseNoChek(k));
			}
		} else {//bail out
			throw new IllegalArgumentException(
				"type "+v.getClass().getName() + " not supported @ "+pathMap.soFar());
		}
	}

	private static class PathMapFactory {
		private String pathSpec;

		protected PathMapFactory(String spec) {
			pathSpec = spec;
		}
		public PathMap create() {
			return new PathMap(pathSpec);
		}

		public static PathMap defaultPathMap() {
			return new PathMap("");
		}
	}

	public void setExpandedMaps(Map<String, String> maps) {
		this.expandedMaps = new HashMap<String, PathMapFactory>();
		for(Entry<String, String> entry : maps.entrySet()) {
			this.expandedMaps.put(entry.getKey(), new PathMapFactory(entry.getValue()));
		}
	}

	public void setNakedMaps(Map<String, String> maps) {
		this.nakedMaps = new HashMap<String, PathMapFactory>();
		for(Entry<String, String> entry : maps.entrySet()) {
			this.nakedMaps.put(entry.getKey(), new PathMapFactory(entry.getValue()));
		}
	}

	public static ObjectExpander defaultExpander() {
		ObjectExpander o = new ObjectExpander();
		return o;
	}

	public void setDiscardedKeys(String discardedKeysCsv) {
		this.discardedKeys = Strings.splitAndTrim(discardedKeysCsv,",");
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}
}
