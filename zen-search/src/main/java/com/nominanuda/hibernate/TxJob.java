package com.nominanuda.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class TxJob<T> {

	protected abstract T job(Session session, Transaction tx) throws Exception;
	
	public T callAndClose(Session s) throws Exception {
		return call(s, true);
	}
	public T call(Session session, boolean close) throws Exception {
		Transaction tx = null;
		T result = null;
		try {
			tx = session.beginTransaction();
			result = job(session, tx);
			tx.commit();
			return result;
		} catch(Exception e) {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			if(session != null && close) {
				session.close();
			}
		}

	}
}
