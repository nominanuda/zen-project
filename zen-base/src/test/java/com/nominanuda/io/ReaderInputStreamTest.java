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
import java.io.StringReader;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;


public class ReaderInputStreamTest {
	private IOHelper io = new IOHelper();
	@Test
	public void testReaderInputStream() throws IOException {
		int times = 16;
		String seed = "la vispaàé";
		String s = doubleNtimes(seed, times);
		int size = new Double(seed.length() * Math.pow(2, times)).intValue();
		Assert.assertEquals(size, s.length());
		ReaderInputStream ris =
			new ReaderInputStream(new StringReader(s), "UTF-8");
		String s1 = io.readAndCloseUtf8(ris);
		Assert.assertEquals(size, s1.length());
		Assert.assertEquals(s, s1);
		ReaderInputStream ris2 =
			new ReaderInputStream(new StringReader(s), "ISO-8859-1");
		String s2 = io.readAndClose(ris2, Charset.forName("ISO-8859-1"));
		Assert.assertEquals(size, s2.length());
		Assert.assertEquals(s, s2);
	}
	private String doubleNtimes(String s, int times) {
		return (times == 0) ? s : doubleNtimes(s + s, times - 1);
	}
}
