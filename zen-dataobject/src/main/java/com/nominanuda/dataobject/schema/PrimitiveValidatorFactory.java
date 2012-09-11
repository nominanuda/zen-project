package com.nominanuda.dataobject.schema;

import com.nominanuda.lang.Fun1;

public interface PrimitiveValidatorFactory {
	String ANYPRIMITIVE = "anyprimitive";
	
	Fun1<Object, String> create(String primitiveTypeDef);
}
