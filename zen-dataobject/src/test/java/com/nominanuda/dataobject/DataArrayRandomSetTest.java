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
package com.nominanuda.dataobject;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DataArrayRandomSetTest {

	@Test
	public void testArrayImp() {
		DataArray aaa = new DataArrayImpl();
		aaa.with("1").with("2").with("3").with("4").with("5");
		aaa.remove(1);
		assertEquals("3", aaa.get(1));
	}

	@Test
	public void testLazyArray() {
		DataArray aaa = new LazyDataArray("[\"1\",\"2\",\"3\",\"4\",\"5\"]");
		aaa.remove(1);
		assertEquals("3", aaa.get(1));
	}
}
