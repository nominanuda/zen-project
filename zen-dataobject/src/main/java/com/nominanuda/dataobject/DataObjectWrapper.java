package com.nominanuda.dataobject;

public interface ObjWrapper {
	Obj unwrap();

	static <T extends ObjWrapper> boolean deepEquals(T o1, T o2) {
		return o1.unwrap().equals(o2.unwrap());
	}
}
