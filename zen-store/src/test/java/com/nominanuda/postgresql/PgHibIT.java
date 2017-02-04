package com.nominanuda.postgresql;

import static com.nominanuda.dataobject.DataStructHelper.Z;
import static java.lang.System.currentTimeMillis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.hibernate.HibernateConfiguration;

//@Ignore
public class PgHibIT {

	private static final String DB_USER = "zen";
	private static final String DB_PASS = "zen";
	private static final String CONN_STRING = "jdbc:postgresql://127.0.0.1:5432/zentest";

	//CREATE TABLE testjson(id bigint NOT NULL, jsonproperty jsonb, CONSTRAINT myentity_pkey PRIMARY KEY (id))
	@Test
	public void testJdbc() throws Exception {
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection(CONN_STRING, DB_USER,
				DB_PASS);
		PreparedStatement q = c.prepareStatement("SELECT * FROM testjson");
		ResultSet rs = q.executeQuery();
		rs.close();
		c.close();
	}

	@Test
	public void testHib() throws Exception {
		HibernateConfiguration cfg = new HibernateConfiguration();
		cfg.setConnectionUrl(CONN_STRING);
		cfg.setUsername(DB_USER);
		cfg.setPassword(DB_PASS);
		cfg.setShowSql(true);
		cfg.setResource("com/nominanuda/postgresql/test.hbm.xml");

		SessionFactory sf = cfg.getSessionFactory();
		Session s = sf.openSession();
		Transaction t = s.beginTransaction();
		Map<String, Object> m = new HashMap<>();
		m.put("id", 1L);
		m.put("jsonproperty", Z.parse("{foo:'bar'}", true));
		s.saveOrUpdate("MyEntity", m);
		Query<?> q = s.createQuery("from MyEntity");
		List<?> rs = q.getResultList();
		for(Object o : rs) {
			System.err.println(o.getClass());
			DataObject oo = (DataObject)((Map)o).get("jsonproperty");
			System.err.println(oo.toString());
			Assert.assertEquals("bar", oo.getString("foo"));
		}
		t.commit();
		s.close();
	}

}
