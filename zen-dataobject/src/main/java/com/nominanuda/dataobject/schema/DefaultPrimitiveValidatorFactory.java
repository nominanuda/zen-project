package com.nominanuda.dataobject.schema;

import com.nominanuda.lang.Fun1;

//TODO existential qualifiers
public class DefaultPrimitiveValidatorFactory implements
		PrimitiveValidatorFactory {

	@Override
	public Fun1<Object, String> create(String primitiveTypeDef) {
		if("n".equals(primitiveTypeDef)) {
			return new Fun1<Object, String>() {
				public String apply(Object param) {
					return param instanceof Number ? null : "not a number";
				}
			};
		} else if(ANYPRIMITIVE.equals(primitiveTypeDef)) {
			return new Fun1<Object, String>() {
				public String apply(Object param) {
					return null;
				}
			};
		} else if("s".equals(primitiveTypeDef)) {
			return new Fun1<Object, String>() {
				public String apply(Object param) {
					return param instanceof String ? null : "not a string";
				}
			};
		} else if("b".equals(primitiveTypeDef)) {
			return new Fun1<Object, String>() {
				public String apply(Object param) {
					return param instanceof Boolean ? null : "not a boolean";
				}
			};
		} else {
			throw new IllegalArgumentException(primitiveTypeDef+" not found");//TODO better msg
		}
	}

}
