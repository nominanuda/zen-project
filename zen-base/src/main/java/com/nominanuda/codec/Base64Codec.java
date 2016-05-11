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

import java.io.IOException;

import com.nominanuda.code.ThreadSafe;

@ThreadSafe
public class Base64Codec {
	public static final Base64Codec B64 = new Base64Codec();
	private static final String[] PADS = {null, "=", "=="};

	public String encodeUrlSafeNoPad(byte[] msg) {
		try {
			return Base64Impl.encodeBytes(msg, Base64Impl.URL_SAFE)
							.replace("=", "");
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public String encodeClassic(byte[] msg) {
		try {
			return Base64Impl.encodeBytes(msg, Base64Impl.NO_OPTIONS);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public String gzipEncodeClassic(byte[] msg) {
		try {
			return Base64Impl.encodeBytes(msg, Base64Impl.GZIP);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public String gzipEncodeUrlSafeNoPad(byte[] msg) {
		try {
			return Base64Impl.encodeBytes(msg,
					Base64Impl.NO_OPTIONS|Base64Impl.GZIP)
					.replace("=", "");
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public byte[] decodeGzipDetect(String msg) {
		return decode(msg, Base64Impl.NO_OPTIONS);
	}
	public byte[] decodeNoGzip(String msg) {
		return decode(msg, Base64Impl.DONT_GUNZIP);
	}
	private byte[] decode(String msg, int options) {
		try {
			int padlen = (4 - (msg.length() % 4))% 4;
			if(padlen == 3)  {
				throw new IllegalArgumentException(
					"unespected base64 message length:"+msg.length());
			}
			if(msg.contains("_") || msg.contains("-")) {
				options = options|Base64Impl.URL_SAFE;
			}
			return Base64Impl.decode((padlen == 0 ? msg : msg+PADS[padlen]), options);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
