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
package com.nominanuda.zen.concurrent;

import static com.nominanuda.zen.common.Maths.MATHS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class ExecTest {
	String testFile = "/tmp/ExecTest"+MATHS.randInt(120000);
	@Test
	public void testThrowingSupplier() throws Exception {
		assertEquals("foo", Exec.exe(() -> {
			new FileOutputStream(new File(testFile)).write("sss".getBytes());
			return "foo";
		}).get());
	}

	@Test
	public void testThrowingRunnable() throws Exception {
		AtomicBoolean passed = new AtomicBoolean(false);
		Exec.exe(() -> {
			new FileOutputStream(new File(testFile)).write("sss".getBytes());
			passed.set(true);
		}).get();
		assertTrue(passed.get());
	}
}
