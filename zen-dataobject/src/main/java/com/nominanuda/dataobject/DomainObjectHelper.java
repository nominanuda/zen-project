package com.nominanuda.dataobject;

import com.nominanuda.lang.Check;

public class DomainObjectHelper {

	public <T extends DataObject> DataObject verify(DataObject o, Class<T> cl) {
		//TODO
		return Check.notNull(o);
	}

	public String domainTypeName(Class<? extends DataObject> t) {
		return t.getSimpleName().toLowerCase();
	}
}
