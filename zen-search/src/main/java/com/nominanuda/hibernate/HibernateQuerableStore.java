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

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;

public class HibernateQuerableStore extends AbstractHibernateStructStore {
	public DataArray query(String type, String hql, DataObject params, int start, int count, String viewName) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			session.setDefaultReadOnly(true);
			tx = session.beginTransaction();
			Query q = session.createQuery(hql)
				.setReadOnly(true)
				.setFirstResult(start)
				.setMaxResults(count);
			for(String k : params.getKeys()) {
				bind(q, k, params.get(k));
			}
			List<?> l = q.list();
			return render(l, viewName);
		} catch(Exception e) {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			if(tx != null && ! tx.wasRolledBack()) {
				tx.rollback();
			}
			if(session != null) {
				session.close();
			}
		}

	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	public Object queryForOne(String type, String hql, DataObject params, boolean expand) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(hql)
						.setReadOnly(true);
			for(String k : params.getKeys()) {
				bind(q, k, params.get(k));
			}
			Object o = q.uniqueResult();
			if(o == null) {
				return null;
			} else if(o instanceof Map<?,?>) {
				Map<String,Object> m = (Map<String,Object>)o;
				return render(m, type);
			} else {
				return o;
			}
		} catch(Exception e) {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			if(tx != null && ! tx.wasRolledBack()) {
				tx.rollback();
			}
			if(session != null) {
				session.close();
			}
		}

	}

	public DataObject byId(String type, String id) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Map<String, Object> obj = (Map<String, Object>)session.get(type, id);
			if (obj == null) {
				return null;
			} else {
				return render(obj, type);
			}
			
		} catch(Exception e) {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			if(tx != null && ! tx.wasRolledBack()) {
				tx.rollback();
				//tx.commit();
			}
			if(session != null) {
				session.close();
			}
		}

	}


}
