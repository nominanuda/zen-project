package com.nominanuda.hibernate;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.web.http.HttpProtocol;

public class HibernateDataObjectStore extends AbstractHibernateStructStore implements HttpProtocol {
//	protected Logger log = LoggerFactory.getLogger(HibernateDataObjectStore.class);

	public DataObject get(String type, String id) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery("from "+type+" where id=:id");
//							.setReadOnly(true);
			DataObject params = new DataObjectImpl();
			params.put("id", id);
			for(String k : params.getKeys()) {
				bind(q, k, params.get(k));
			}
			Object o = q.uniqueResult();
			if(o == null) {
				return null;
			} else if(o instanceof Map<?,?>) {
				Map<String,Object> m = (Map<String,Object>)o;
				postprocess(m, type, true);
				return objectExpander.expand(m, type, true);
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

	public void put(String type, DataObject o) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
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

	public void put(String type, DataObject o, Session session, Transaction tx) throws Exception {
		//TODO
		o.put("tstamp", System.currentTimeMillis());
		Map<String, ? super Object> entity = struct.toMapsAndSetLists((DataObject)o);
		session.saveOrUpdate(type, entity);
	}

	public void remove(String type, String id) throws Exception {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
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
		return sessionFactory.openSession();
	}

}
