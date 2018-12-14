/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.apikey;

import java.nio.ByteBuffer;

import static com.nominanuda.zen.codec.Base62.B62;

public class XorCipher implements Cipher {
	public static final String XOR = "xor";
	private String secret = "hd7k9Hp106TVghS7LPoBG6ywl734RTOpya";
	private byte[] barr = B62.decode(secret);

	@Override
	public byte[] encrypt(byte[] clearText) {
		return bitwiseXor(clearText);
	}

	private byte[] bitwiseXor(byte[] clearText) {
		ByteBuffer res = ByteBuffer.allocate(clearText.length);
		int i = 0;
		for(byte b : clearText) {
			byte c  = (byte)(b ^ barr[i++ % barr.length]);
			res.put(c);
		}
		return res.array();
	}

	@Override
	public byte[] decrypt(byte[] cypherText) {
		return bitwiseXor(cypherText);
	}

	public void setSecret(String secret) {
		this.barr = B62.decode(secret);
	}

}
