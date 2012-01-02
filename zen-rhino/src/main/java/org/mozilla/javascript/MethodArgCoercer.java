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
package org.mozilla.javascript;

public interface MethodArgCoercer {

	Object REGULAR_COERCE_OP = new Object();

	/**
	 * enhanced implementation of {@link NativeJavaObject#getConversionWeight}
	 * @param value
	 * @param type
	 * @return the conversion weight or Integer.MIN_VALUE if correct 
	 *         value should be obtained from NativeJavaObject#getConversionWeight
	 */
	int getConversionWeight(Object value, Class<?> type);

	/**
	 * enhanced implementation of {@link NativeJavaObject#coerceTypeImpl}
	 * @param type
	 * @param value
	 * @return the coerced value or {@link RhinoEmbedding#REGULAR_COERCE_OP} if 
	 *         value should be obtained from NativeJavaObject#getConversionWeight
	 */
	Object coerceTypeImpl(Class<?> type, Object value);
}
