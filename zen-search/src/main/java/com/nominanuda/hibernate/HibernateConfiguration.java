package com.nominanuda.hibernate;

import java.beans.PropertyVetoException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.internal.EventListenerRegistryImpl;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.metamodel.MetadataSources;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nominanuda.lang.Check;

public class HibernateConfiguration {
	private String username;
	private String password;
	private String connectionUrl;
	private Configuration cfg;
	private SessionFactory sessionFactory;
	private Boolean showSql = Boolean.TRUE;
	private ComboPooledDataSource ds = null;
	private List<String> resources = new LinkedList<String>();
	//val an be of type X OR Iterable<X>
	//where X is of type SaveOrUpdateEventListener or Class<SaveOrUpdateEventListener>
	private Map<String,Object> listeners = new LinkedHashMap<String, Object>();
	private final ServiceRegistry serviceRegistry;
	private final MetadataSources metadataSources;
	private boolean dynamic = true;
	private boolean c3p0 = false;

	public void setC3p0(boolean c3p0) {
		this.c3p0 = c3p0;
	}
	public enum DBType {
		MYSQL, HSQLDB
	}
	public HibernateConfiguration() {
		serviceRegistry = new ServiceRegistryBuilder().addService(EventListenerRegistry.class, new EventListenerRegistryImpl()).buildServiceRegistry();
		metadataSources = new MetadataSources(serviceRegistry);
	}

	private void addListener(EventType<SaveOrUpdateEventListener> t, Object l, EventListenerRegistry r) {
		if(l instanceof SaveOrUpdateEventListener) {
			r.appendListeners(t, (SaveOrUpdateEventListener)l);
		} else {
			r.appendListeners(t, (Class<SaveOrUpdateEventListener>)l);
		}
	}
	public Configuration getConfiguration() {
		if(cfg == null) {
			cfg = makeConfiguration();
		}
		return cfg;
	}

	public SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			sessionFactory = getConfiguration().buildSessionFactory(serviceRegistry);
		}
		return sessionFactory;
	}

	private Configuration makeConfiguration() {
		for(String res : resources) {
			metadataSources.addResource(res);
		}
		//SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
		for(Entry<String, ?> e : listeners.entrySet()) {
			String evName = e.getKey().toLowerCase().replace('_', '-');
			@SuppressWarnings("unchecked")
			EventType<SaveOrUpdateEventListener> evt = Check.notNull(EventType.eventTypeByNameMap.get(evName));
			Object val = e.getValue();
			if(val instanceof Iterable<?>) {
				for(Object l : (Iterable<?>)val) {
					addListener(evt, l, listenerRegistry);
				}
			} else {
				addListener(evt, val, listenerRegistry);
			}
		}
		Configuration cfg = new Configuration();
		DBType dbType = inferDbType();
		//Configuration cfg = new Configuration();
		if(dynamic ) {
			cfg.setProperty("hibernate.default_entity_mode", "dynamic-map");
		};
		cfg.setProperty("hibernate.show_sql", showSql .toString())
		.setProperty("hibernate.connection.url", connectionUrl)
		.setProperty("hibernate.connection.username", username)
		.setProperty("hibernate.connection.password", password);
		//TODO pluggable
		if(c3p0) {
			cfg
			.setProperty("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider")
			.setProperty("hibernate.c3p0.acquire_increment", "3")
			.setProperty("hibernate.c3p0.min_size", "3")
			.setProperty("hibernate.c3p0.max_size", "15")
			.setProperty("hibernate.c3p0.max_statements", "300")
			.setProperty("hibernate.c3p0.acquireRetryAttempts", "2")
			.setProperty("hibernate.c3p0.acquireRetryDelay", "450")
			.setProperty("hibernate.c3p0.timeout", "5000")
			.setProperty("hibernate.c3p0.idle_test", "300")
			.setProperty("hibernate.connection.driver_class", getDriverClass(dbType));
		}
		
		
//		cfg.setListener("delete", new HibernateDeleteListener());
		switch (dbType) {
			case HSQLDB:
				cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
					.setProperty("hibernate.jdbc.batch_size", "0");
				break;
			case MYSQL:
				cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
				break;
			default:
				throw new IllegalStateException();
		}
		return cfg;
	}

	private DBType inferDbType() {
		return connectionUrl.contains("mysql") ? DBType.MYSQL : DBType.HSQLDB;
	}

	public void setConnectionUrl(String url) {
		this.connectionUrl = url;
	}

	public void setUsername(String user) {
		this.username = user;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public void setShowSql(Boolean showSql) {
		this.showSql = showSql;
	}
	
	private String getDriverClass(DBType dbType) {
		switch (dbType) {
		case HSQLDB:
			return "org.hsqldb.jdbcDriver";
		case MYSQL:
			return "com.mysql.jdbc.Driver";
		default:
			throw new IllegalStateException();
		}
	}
	public DataSource getDataSource() throws PropertyVetoException {//TODO not c3p0  or configurable
		if(ds == null) {
			ds = new ComboPooledDataSource();
			ds.setDriverClass(getDriverClass(inferDbType()));
			ds.setJdbcUrl(connectionUrl);
			ds.setUser(username);
			ds.setPassword(password);
			ds.setAcquireIncrement(3);
			ds.setMinPoolSize(3);
			ds.setMaxPoolSize(15);
			ds.setMaxStatements(300);
			ds.setAcquireRetryAttempts(2);
			ds.setAcquireRetryDelay(450);
			ds.setCheckoutTimeout(5000);
			ds.setIdleConnectionTestPeriod(300);
		}
		return ds;
	}

	public void setResource(String resource) {
		this.resources.clear();
		this.resources.add(resource);
	}
	public void setResources(List<String> resources) {
		this.resources.clear();
		this.resources.addAll(resources);
	}
	public void setListeners(Map<String,?> m) {
		this.listeners.clear();
		this.listeners.putAll(m);
	}
}
