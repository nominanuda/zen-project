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


import java.util.HashMap;
import java.util.Map;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import static org.junit.Assert.*;


import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataview.DataView;
import com.nominanuda.dataview.MapPropertyReader;
import com.nominanuda.hibernate.HibernateConfiguration;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

public class PersistTest {

	@Test
	public void test() throws Exception {
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
		cfg.setResource("com/nominanuda/hibernate/sample.hbm.xml");
		
		new SchemaExport(cfg.getConfiguration()).create(true, true);
		HibernateDataObjectStore hdos = new HibernateDataObjectStore();
		hdos.setSessionFactory(cfg.getSessionFactory());
		DataView<Map<String,? extends Object>> dataView = new DataView<Map<String,? extends Object>>();
		dataView.setPropertyReader(new MapPropertyReader());
		dataView.setViewDef("id,title");
		Map<String, DataView<Map<String,? extends Object>>> dataViewRegistry = new HashMap<String, DataView<Map<String,? extends Object>>>();
		dataViewRegistry.put("User", dataView);
		hdos.setDataViewRegistry(dataViewRegistry);
		hdos.put("User", STRUCT.parseObject("{id:'1',title:'Mr. Nothing'}", true));

		HibernateQuerableStore qStore =new HibernateQuerableStore();

		qStore.setDataViewRegistry(dataViewRegistry);
		qStore.setSessionFactory(cfg.getSessionFactory());
		DataObject user = qStore.byId("User", "1");
		assertEquals("Mr. Nothing", user.getString("title"));
	}

}
