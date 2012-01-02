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
package com.nominanuda.io;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;


public class BufferingTest {
	private IOHelper io = new IOHelper();

	@Test
	public void testBufferingInputStream() throws IOException {
		InputStream is1 = getClass().getResourceAsStream(getClass().getSimpleName()+".class");
		byte[] barr = io.readAndClose(is1);
		InputStream is = getClass().getResourceAsStream(getClass().getSimpleName()+".class");
		BufferingInputStream bis = new BufferingInputStream(is);
		byte[] barr1 = io.read(bis, false);
		Assert.assertArrayEquals(barr, barr1);
		bis.reset();
		byte[] barr2 = io.read(bis, false);
		Assert.assertArrayEquals(barr, barr2);
		bis.close();
		try {
			bis.reset();
			Assert.fail();
		} catch(IllegalStateException e) {}
	}
}
