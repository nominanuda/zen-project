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

import static com.nominanuda.zen.common.Maths.MATHS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.nominanuda.dataview.DataView;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;

public abstract class AbstractHibernateStructStore {
	protected SessionFactory sessionFactory;
	protected Map<String, DataView<Map<String, ? extends Object>>> dataViewRegistry = new HashMap<String, DataView<Map<String, ? extends Object>>>();

	@SuppressWarnings("unchecked")
	public Arr render(List<?> l, String type) {
		Arr a = Arr.make();
		for(Object o : l) {
			a.add(o instanceof Map<?,?>
				? render((Map<String, Object>)o, type)
				: o
			);
		}
		return a;
	}

	protected Obj render(Map<String, Object> obj, String type /*TODO boolean expand*/) {
		return obj == null ? null : dataViewRegistry.get(type).render(obj);
	}

	protected void bind(Query<?> q, String k, Object v) {
		JsonType t = JsonType.of(v);
		switch (t) {
		case arr:
			q.setParameterList(k, (List<?>)v);
			break;
		case obj:
			q.setEntity(k, (Map)v);
			break;
		case str:
			q.setString(k, (String)v);
			break;
		case bool:
			q.setBoolean(k, (Boolean)v);
			break;
		case num:
			Double d = ((Number)v).doubleValue();
			if(MATHS.isInteger(d)) {
				q.setLong(k, d.longValue());
			} else {
				q.setDouble(k, d);
			}
			break;
		default:
			throw new IllegalArgumentException(t.name());
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setDataViewRegistry(
			Map<String, DataView<Map<String, ? extends Object>>> dataViewRegistry) {
		this.dataViewRegistry = dataViewRegistry;
	}


}