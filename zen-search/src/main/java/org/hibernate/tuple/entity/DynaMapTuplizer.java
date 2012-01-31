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
	protected Instantiator buildInstantiator(PersistentClass mappingInfo) {
		return new DynaMapDynamicMapInstantiator( mappingInfo );
		//return super.buildInstantiator(mappingInfo);
	}
}
