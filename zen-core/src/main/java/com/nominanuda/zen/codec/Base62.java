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

import static com.nominanuda.zen.codec.Base64Codec.B64;
import static com.nominanuda.zen.common.Str.UTF8;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Base62 {
	public static final Base62 B62 = new Base62();

	public String encodeUtf8(String s) {
		return encode(s.getBytes(UTF8));
	}
	
	public String encodeHex(String hex) {
		return encode(Hex.decode(hex));
	}

	public String encode(byte[] b) {
		char[] b64 = B64.encodeUrlSafeNoPad(b).toCharArray();
		char[] b62 = new char[b64.length*2];
		int count = 0;
		for(char c : b64) {
			switch (c) {
			case 'x':
				b62[count++] = 'x';
				b62[count++] = 'x';
				break;
			case '-':
				b62[count++] = 'x';
				b62[count++] = '1';
				break;
			case '_':
				b62[count++] = 'x';
				b62[count++] = '2';
				break;
			default:
				b62[count++] = c;
				break;
			}
		}
		return new String(b62,0,count);
	}
	

	public String decodeUtf8(String s) {
		return new String(decode(s), UTF8);
	}
	
	public String decodeHex(String s) {
		return Hex.encode(decode(s));
	}

	public byte[] decode(String s) {
		char[] b62 = s.toCharArray();
		char[] b64 = new char[b62.length];
		int count = 0;
		boolean escapeMode = false;
		for(char c : b62) {
			switch (c) {
			case 'x':
				if(escapeMode) {
					b64[count++] = 'x';
				}
				escapeMode = !escapeMode;
				break;
			case '1':
				if(escapeMode) {
					b64[count++] = '-';
					escapeMode = false;
				} else {
					b64[count++] = '1';
				}
				break;
			case '2':
				if(escapeMode) {
					b64[count++] = '_';
					escapeMode = false;
				} else {
					b64[count++] = '2';
				}
				break;
			default:
				b64[count++] = c;
				break;
			}
		}
		String b64s = new String(b64, 0, count);
		return  B64.decodeNoGzip(b64s);
	}
}
