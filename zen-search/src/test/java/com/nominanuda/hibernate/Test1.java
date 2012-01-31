package com.nominanuda.hibernate;


import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;

import com.nominanuda.hibernate.HibernateConfiguration;

public class Test1 {

	@Test
	public void test() {
//		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()./*configure().*/buildServiceRegistry();
//		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
//		metadataSources.addResource("com/nominanuda/hib/sample.hbm.xml");
//		SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
//		
//		Configuration configuration = new Configuration().configure();
//		SessionFactoryImpl sessionFactory2 = (SessionFactoryImpl) configuration.buildSessionFactory(serviceRegistry);
//		EventListenerRegistry listenerRegistry = sessionFactory2.getServiceRegistry().getService(EventListenerRegistry.class);
//		SaveOrUpdateEventListener indexListener = null;//new SolrIndexEventListener(); // a SaveOrUpdateEventListener i wanted to attach
//		listenerRegistry.appendListeners(EventType.SAVE_UPDATE, indexListener);
		
		
		HibernateConfiguration cfg = new HibernateConfiguration();
		cfg.setConnectionUrl("jdbc:hsqldb:mem:sampledb");
		cfg.setUsername("SA");
		cfg.setPassword("");
		cfg.setShowSql(true);
		cfg.setResource("com/nominanuda/hib/sample.hbm.xml");
		
		new SchemaExport(cfg.getConfiguration()).create(true, true);
	}

}
