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
package com.nominanuda.zen.codec;


public class Hex {
	private static final char[] ALPHABET = new char[]
		{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	/**
	 * 
	 * @param barr
	 * @return the hex string uppercase
	 */
	public static String encode(byte[] barr) {
		char[] result = new char[barr.length * 2];
		for (int i = 0; i < barr.length; i++) {
			int v = barr[i] & 0xFF;
			result[i * 2] = ALPHABET[v >>> 4];
			result[i * 2 + 1] = ALPHABET[v & 0x0F];
		}
		return new String(result);
	}

	public static byte[] decode(String hex) {
		int len = hex.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
		}
		return bytes;
	}

}
