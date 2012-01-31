package com.nominanuda.hibernate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataArrayImpl;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.DataType;
import com.nominanuda.lang.Maths;

public abstract class AbstractHibernateStructStore {
	protected static final DataStructHelper struct = new DataStructHelper();
	protected SessionFactory sessionFactory;
	protected ObjectExpander objectExpander = new ObjectExpander();

	protected DataArray postprocess(List<?> l, String type, boolean expand) {
		DataArrayImpl a = new DataArrayImpl();
		for(Object o : l) {
			a.add(o instanceof Map<?,?>
				? postprocess((Map<String, Object>)o, type, expand)
				: o
			);
		}
		return a;
	}

	protected DataObject postprocess(Map<String, Object> obj, String type, boolean expand) {
		return obj == null ? null : objectExpander.expand(obj, type, expand);
	}

	protected void bind(Query q, String k, Object v) {
		DataType t = struct.getDataType(v);
		switch (t) {
		case array:
			List l = new LinkedList();
			Iterator i = ((DataArrayImpl)v).iterator();
			while(i.hasNext()) {
				l.add(i.next());
			}
			q.setParameterList(k, l);
			break;
		case string:
			q.setString(k, (String)v);
			break;
		case bool:
			q.setBoolean(k, (Boolean)v);
			break;
		case number:
			Double d = ((Number)v).doubleValue();
			if(Maths.isInteger(d)) {
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

	public void setObjectExpander(ObjectExpander objectExpander) {
		this.objectExpander = objectExpander;
	}

}