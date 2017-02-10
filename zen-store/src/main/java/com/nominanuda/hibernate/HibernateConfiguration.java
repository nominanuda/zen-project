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

import static com.nominanuda.lang.Check.illegalargument;
import static java.util.Arrays.asList;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nominanuda.lang.Check;
import com.nominanuda.postgresql.PgDataObjectJsonType;
import com.nominanuda.postgresql.PgMapListJsonType;

public class HibernateConfiguration {
	private String username;
	private String password;
	private String connectionUrl;
	private Configuration cfg;
	private SessionFactory sessionFactory;
	private Boolean showSql = Boolean.TRUE;
	private ComboPooledDataSource ds = null;
	private List<String> resources = new LinkedList<String>();
	private String currentSessionContext = "managed";//thread jta
	//val an be of type X OR Iterable<X>
	//where X is of type SaveOrUpdateEventListener or Class<SaveOrUpdateEventListener>
	private boolean dynamic = true;
	private boolean c3p0 = false;

	public void setC3p0(boolean c3p0) {
		this.c3p0 = c3p0;
	}
	public enum DBType {
		MYSQL, HSQLDB, POSTGRESQL, UNKNOWN
	}
	public HibernateConfiguration() {
//		serviceRegistry = new ServiceRegistryBuilder().addService(EventListenerRegistry.class, new EventListenerRegistryImpl()).buildServiceRegistry();
//		metadataSources = new MetadataSources(serviceRegistry);
	}

	public Configuration getConfiguration() {
		if(cfg == null) {
			cfg = makeConfiguration();
		}
		return cfg;
	}

	public SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			Configuration cfg = getConfiguration();
			sessionFactory = cfg.buildSessionFactory();//serviceRegistry
		}
		return sessionFactory;
	}

	private Configuration makeConfiguration() {
		//SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		final Configuration cfg = new Configuration();
		DBType dbType = inferDbType();
		//Configuration cfg = new Configuration();
		if(dynamic ) {
			cfg.setProperty("hibernate.default_entity_mode", "dynamic-map");
		};
		cfg.setProperty("hibernate.current_session_context_class", currentSessionContext);
		cfg.setProperty("hibernate.show_sql", showSql .toString())
		.setProperty("hibernate.connection.url", connectionUrl)
		.setProperty("hibernate.connection.username", username)
		.setProperty("hibernate.connection.password", password)
		.setProperty("hibernate.connection.useUnicode", "true")
		.setProperty("hibernate.connection.characterEncoding", "UTF-8")
		.setProperty("hibernate.connection.charSet", "UTF-8")
		.setProperty("hibernate.connection.driver_class", getDriverClass(dbType))
		;

		//TODO pluggable
		if(c3p0) {
			cfg
			.setProperty("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider")
			.setProperty("hibernate.c3p0.acquire_increment", "3")
			.setProperty("hibernate.c3p0.min_size", "30")
			.setProperty("hibernate.c3p0.max_size", "100")
			.setProperty("hibernate.c3p0.max_statements", "300")
			.setProperty("hibernate.c3p0.acquireRetryAttempts", "2")
			.setProperty("hibernate.c3p0.acquireRetryDelay", "450")
			.setProperty("hibernate.c3p0.timeout", "5000")
			.setProperty("hibernate.c3p0.idle_test", "300");
		}
		switch (dbType) {
			case HSQLDB:
				cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
					.setProperty("hibernate.jdbc.batch_size", "0");
				break;
			case MYSQL:
				cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
				//.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
				;
				break;
			case POSTGRESQL:
				cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
				cfg.registerTypeOverride( new PgDataObjectJsonType(), new String[]{"jsonDataObject"});
				cfg.registerTypeOverride( new PgMapListJsonType(), new String[]{"jsonMapList"});
				break;
			default:
				throw new IllegalStateException();
		}
		Properties properties = cfg.getProperties();
		Environment.verifyProperties( properties );
		for(String res : resources) {
			cfg.addResource(res);
		}
		return cfg;
	}

	private DBType inferDbType() {
		return connectionUrl.contains("mysql") ? DBType.MYSQL
			: connectionUrl.contains("hsql") ? DBType.HSQLDB
			: connectionUrl.contains("postgresql") ? DBType.POSTGRESQL
			: DBType.UNKNOWN;
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
		case POSTGRESQL:
			return "org.postgresql.Driver";
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

	private static final Set<String> ALLOWED_CURRENT_SESSION_CONTEXTS = new HashSet<>(asList("thread","managed","jta"));
	public void setCurrentSessionContext(String currentSessionContext) {
		illegalargument.assertTrue(ALLOWED_CURRENT_SESSION_CONTEXTS.contains(currentSessionContext), "unsupported currentSessionContext:"+currentSessionContext);
		this.currentSessionContext = currentSessionContext;
	}
}
