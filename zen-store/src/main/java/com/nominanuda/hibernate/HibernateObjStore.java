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

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.zen.obj.Obj;

public class HibernateObjStore extends AbstractHibernateStructStore implements HttpProtocol {
//	protected Logger log = LoggerFactory.getLogger(HibernateDataObjectStore.class);

	public Obj get(String type, String id) throws Exception {
		return get(type, id, type);
	}

	public Obj get(String type, String id, String viewName) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = openSession();
			tx = session.beginTransaction();
			Query<?> q = session.createQuery("from "+type+" where id=:id");
//							.setReadOnly(true);
			Obj params = Obj.make();
			params.put("id", id);
			for(String k : params.keySet()) {
				bind(q, k, params.get(k));
			}
			Object o = q.getSingleResult();
			if(o == null) {
				return null;
			} else if(o instanceof Map<?,?>) {
				@SuppressWarnings("unchecked")
				Map<String,Object> m = (Map<String,Object>)o;
				return render(m, viewName);
			} else {
				throw new IllegalStateException();
			}

		} finally {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			if(session != null) {
				session.close();
			}
		}
	}

	public void put(String type, Obj o) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = openSession();
			tx = session.beginTransaction();
			put(type, o, session, tx);
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}

	public void put(String type, Obj o, Session session, Transaction tx) throws Exception {
		//TODO
		Map<String, ? super Object> entity = o;
		session.saveOrUpdate(type, entity);
	}

	public void remove(String type, String id) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = openSession();
			tx = session.beginTransaction();
			Object o = session.get(type, id);
			if(o != null) {
				session.delete(o);
			}
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}

	public Session openSession() {
		Session s = sessionFactory.openSession();
		return s;
	}

}
