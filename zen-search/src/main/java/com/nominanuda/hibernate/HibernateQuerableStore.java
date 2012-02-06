package com.nominanuda.hibernate;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;

public class HibernateQuerableStore extends AbstractHibernateStructStore {
	public DataArray query(String type, String hql, DataObject params, int start, int count, boolean expand) throws Exception {
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
			return render(l, type);
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
