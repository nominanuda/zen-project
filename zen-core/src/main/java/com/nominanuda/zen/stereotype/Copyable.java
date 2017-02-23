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

public interface Copyable {

	/**
	 * @return the copy such as that a.equals(b); if the object is {@link Immutable}
	 * than {@link #copy()} can return itself so that a == b
	 */
	Object copy();

	/**
	 * cast friendly version
	 * @return same as {@link Copyable#copy()}
	 * @throws ClassCastException
	 */
	@SuppressWarnings("unchecked")
	default <T> T copyCast() throws ClassCastException {
		return (T)copy();
	}
}
