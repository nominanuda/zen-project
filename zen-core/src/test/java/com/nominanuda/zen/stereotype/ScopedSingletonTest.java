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

import static org.junit.Assert.*;

import org.junit.Test;

public class ScopedSingletonTest {

	@Test
	public void test() {
		ScopedSingletonFactory ssf = ScopedSingletonFactory.getInstance();
		Object appScope1 = new Object();
		Object appScope2 = new Object();
		ScopedSingletonTest sing0 = ssf.buildJvmSingleton(ScopedSingletonTest.class);
		ScopedSingletonTest sing1 = ssf.buildScopedSingleton(appScope1, ScopedSingletonTest.class);
		ScopedSingletonTest sing2 = ssf.buildScopedSingleton(appScope2, ScopedSingletonTest.class);
		assertNotEquals(sing0, sing1);
		assertNotEquals(sing1, sing2);
	}

	@Test
	public void test2() {
		ScopedSingletonFactory ssf = ScopedSingletonFactory.getInstance();
		Object appScope1 = "1samename".substring(1);
		Object appScope2 = "2samename".substring(1);
		ScopedSingletonTest sing01 = ssf.buildJvmSingleton(ScopedSingletonTest.class);
		ScopedSingletonTest sing02 = ssf.buildJvmSingleton(ScopedSingletonTest.class);
		ScopedSingletonTest sing1 = ssf.buildScopedSingleton(appScope1, ScopedSingletonTest.class);
		ScopedSingletonTest sing2 = ssf.buildScopedSingleton(appScope1, ScopedSingletonTest.class);
		assertNotSame(appScope1, appScope2);
		assertEquals(sing01, sing02);
		assertEquals(sing1, sing2);
	}

}
