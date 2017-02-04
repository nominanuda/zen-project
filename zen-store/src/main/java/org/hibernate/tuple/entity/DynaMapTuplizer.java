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
package org.hibernate.tuple.entity;


import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.entity.DynamicMapEntityTuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;

public class DynaMapTuplizer extends DynamicMapEntityTuplizer {

	public DynaMapTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
		super(entityMetamodel, mappedEntity);
	}

	@Override
	protected Instantiator buildInstantiator(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
		return new DynaMapDynamicMapInstantiator( mappingInfo );
		//return super.buildInstantiator(mappingInfo);
	}
}
