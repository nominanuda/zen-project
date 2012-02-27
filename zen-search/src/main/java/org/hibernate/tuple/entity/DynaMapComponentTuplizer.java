package org.hibernate.tuple.entity;

import org.hibernate.mapping.Component;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.component.DynamicMapComponentTuplizer;

public class DynaMapComponentTuplizer extends DynamicMapComponentTuplizer {
	private static final long serialVersionUID = 7600773625965940439L;

	public DynaMapComponentTuplizer(Component component) {
		super(component);
	}

	@Override
	protected Instantiator buildInstantiator(Component component) {
		return new DynaMapDynamicMapInstantiator();
	}

}
