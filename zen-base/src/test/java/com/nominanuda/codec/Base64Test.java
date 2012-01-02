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
package com.nominanuda.codec;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import static org.junit.Assert.*;

public class Base64Test {
	private static final Base64Codec base64 = new Base64Codec();

	@Test
	public void testUrlSafe() throws UnsupportedEncodingException {
		String[] ss = new String[] {"la","la1","la22","la333","la4444","la55555",};
		for(String s : ss) {
			doTest(s);
		}
	}
	@Test
	public void testGzipDetectCollision() throws UnsupportedEncodingException {
		int numbytes = 128;
		for(int i = 0; i < 1000; i++) {
			doTest(rand(numbytes));
		}
	}
	private void doTest(String s) throws UnsupportedEncodingException {
		for(String enc : new String[]{"UTF-8","UTF-16","ISO-8859-1","US-ASCII"}) {
			byte[] msg = s.getBytes(enc);
			doTest(msg);
		}
	}
	private void doTest(byte[] msg) throws UnsupportedEncodingException {
		assertArrayEquals(msg, base64.decodeGzipDetect(base64.encodeClassic(msg)));
		assertArrayEquals(msg, base64.decodeGzipDetect(base64.gzipEncodeClassic(msg)));
		assertArrayEquals(msg, base64.decodeGzipDetect(base64.encodeUrlSafeNoPad(msg)));
		assertArrayEquals(msg, base64.decodeGzipDetect(base64.gzipEncodeUrlSafeNoPad(msg)));
	}
	private byte[] rand(int numbytes) {
		byte[] res = new byte[numbytes];
		for(int i = 0; i < numbytes; i++) {
			res[i] = new Double(Math.random() * 255).byteValue(); 
		}
//		System.err.println(Hex.encode(res));
		return res;
	}
}
