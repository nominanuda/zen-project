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
package com.nominanuda.dataobject.schema;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.util.function.Function;


//TODO existential qualifiers
public class DefaultPrimitiveValidatorFactory implements
		PrimitiveValidatorFactory {

	
	@Override
	public Function<Object, String> create(String primitiveTypeDef) {
		if("n".equals(primitiveTypeDef)) {
			return new Function<Object, String>() {
				public String apply(Object param) {
					return param instanceof Number ? null : "not a number";
				}
			};
		} else if(ANYPRIMITIVE.equals(primitiveTypeDef)) {
			return new Function<Object, String>() {
				public String apply(Object param) {
					return null;
				}
			};
		} else if("s".equals(primitiveTypeDef)) {
			return new Function<Object, String>() {
				public String apply(Object param) {
					return param instanceof String ? null : "not a string";
				}
			};
		} else if("b".equals(primitiveTypeDef)) {
			return new Function<Object, String>() {
				public String apply(Object param) {
					return param instanceof Boolean ? null : "not a boolean";
				}
			};
		} else if(DefaultPrimitiveValidatorFactory.ANYPRIMITIVE.equals(primitiveTypeDef)) {
			return new Function<Object, String>() {
				public String apply(Object param) {
					return STRUCT.isPrimitiveOrNull(param) ? null : "not a boolean";
				}
			};
		} else {
			throw new IllegalArgumentException(primitiveTypeDef+" not found");//TODO better msg
		}
	}

}
