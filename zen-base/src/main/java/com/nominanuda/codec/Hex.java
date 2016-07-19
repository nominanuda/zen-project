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

import java.math.BigInteger;


public class Hex {
	public static String encode(byte[] msg) {
		return String.format("%0" + (msg.length << 1) + "x", new BigInteger(1, msg));
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
