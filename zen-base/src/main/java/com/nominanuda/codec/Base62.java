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

import java.nio.charset.Charset;

import com.nominanuda.code.ThreadSafe;


/**
 * warning experimental, will probably change
 *
 */
@ThreadSafe
public class Base62 {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Base64Codec base64 = new Base64Codec();

	public String encodeUtf8(String s) {
		return encode(s.getBytes(UTF8));
	}

	public String encode(byte[] b) {
		char[] b64 = base64.encodeUrlSafeNoPad(b).toCharArray();
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
		return  base64.decodeNoGzip(b64s);
	}
}
