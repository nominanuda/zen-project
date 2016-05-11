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


public class Hex {

	public static String encode(byte[] msg) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < msg.length; i++) {
			int halfbyte = (msg[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					builder.append((char) ('0' + halfbyte));
				} else {
					builder.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = msg[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return builder.toString();
	}

	
//	TODO check if this implementation could be more efficient, and in case FIX IT! because it drops the first '00'
//	(e.g. gives back 50b65b1444cd228f8d636b7ba3556e instead of 0050b65b1444cd228f8d636b7ba3556e)
//	(use com.nominanuda.codec.HexTest for testing)
//	
//	public static String encode(byte[] msg) {
//		String s = new BigInteger(1, msg).toString(16);
//		return s.length() % 2 == 0 ? s : "0" + s;
//	}
}
