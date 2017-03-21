/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.stereotype;

import javax.annotation.concurrent.Immutable;

/**
 * 
 * not necessarily {@link Immutable}
 * 
 */
public interface Value extends Copyable {
	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);

	/**
	 * 
	 * @param o1
	 * @param o2
	 * @return true if o1 and o2 are both null or o1.equals(o2) 
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}
}
