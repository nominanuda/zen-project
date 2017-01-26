package com.nominanuda.dataobject;

public interface DataObjectWrapper {
	DataObject unwrap();

	static <T extends DataObjectWrapper> boolean deepEquals(T o1, T o2) {
		return o1.unwrap().equals(o2.unwrap());
	}
}
