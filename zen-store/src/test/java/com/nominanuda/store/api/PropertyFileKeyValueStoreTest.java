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
package com.nominanuda.store.api;

import static com.nominanuda.zen.oio.OioUtils.IO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class PropertyFileKeyValueStoreTest {

	@Test
	public void test() throws Exception {
		File f = IO.newTmpFile(getClass().getSimpleName());
		PropertyFileKeyValueStore pkvs = new PropertyFileKeyValueStore(f.getAbsolutePath());
		pkvs.init();
		pkvs.put("foo", "bar");
		pkvs.put("foo", "baz");
		assertTrue(pkvs.exists("foo"));
		assertEquals("baz", pkvs.get("foo"));
		pkvs.dispose();
		PropertyFileKeyValueStore pkvs2 = new PropertyFileKeyValueStore(f.getAbsolutePath());
		pkvs2.init();
		assertEquals("baz", pkvs2.get("foo"));
		pkvs2.dispose();
	}

}
