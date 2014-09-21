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

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Collections;

public final class DataView<T> {
	private Collection<String> wildcardProps = Collections.emptySet();// = Arrays.asList("id", "title");
	private static final DataStructHelper struct = new DataStructHelper();
	private DataViewDef viewDef;
	private PropertyReader<T> pReader;// = (PropertyReader<T>)new MapPropertyReader<T>();

	public DataObject render(T obj) {
		Check.illegalargument.assertTrue(pReader.accepts(obj));
		return compileObject(obj, viewDef);
	}
	//TODO array of array not supported
	private DataObject compileObject(T m, DataViewDef viewDef) {
		DataObject res = new DataObjectImpl();
		for(String k : pReader.readableProps(m)) {
			if(! (viewDef.isTraversable(k) || wildcardProps.contains(k))) {
				continue;
			}
			Object v = pReader.read(m, k);
			if(v == null) {
				continue;
			} else if(v instanceof Collection<?>) {
				if(viewDef.isTraversable(k)) {
					DataArray arr = res.putNewArray(k);
					for(Object o : (Collection<?>)v) {
						Object x = evalScalarOrObject(k, o, viewDef);
						arr.add(x);
					}
				}
			} else {
				Object x = evalScalarOrObject(k, v, viewDef);
				if(x != null) {
					res.put(k, x);
				}
			}
		}
		return res;
	}

	private Object evalScalarOrObject(String k, Object v, DataViewDef viewDef) {
		if(v == null) {
			return null;
		} else if(struct.isPrimitiveOrNull(v)) {
			return v;
		} else if(pReader.accepts(v)) {
			T m = (T)v;
			if(viewDef.isTraversable(k)) {
				return compileObject(m, viewDef.traverse(k));
			} else if(hasAny(m, wildcardProps)) {
				DataObjectImpl o = new DataObjectImpl();
				for(String p : wildcardProps) {
					if(pReader.hasProp(m, p)) {
						o.put(p, pReader.read(m, p));
					}
				}
				return o;
			} else {
				return compileObject(m, viewDef.traverseNoChek(k));
			}
		} else {//bail out
			throw new IllegalArgumentException(
				"type "+v.getClass().getName() + " not supported @ "+viewDef.soFar());
		}
	}

	private boolean hasAny(T t, Collection<String> props) {
		for(String p : pReader.readableProps(t)) {
			if(props.contains(p)) {
				return true;
			}
		}
		return false;
	}

	public void setWildcardProps(Collection<String> wildcardProps) {
		this.wildcardProps = wildcardProps;
	}

	public void setViewDef(String viewDef) {
		this.viewDef = new DataViewDef(viewDef);
	}

	public void setPropertyReader(PropertyReader<T> pReader) {
		this.pReader = pReader;
	}


}
