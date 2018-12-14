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

import static com.nominanuda.zen.common.Str.UTF8;

import com.nominanuda.zen.codec.Digester;

public class AesCipher implements Cipher {

	public static final String AES = "aes";
	private final Digester digester;

	public AesCipher(String aesSecret) {
		digester = new Digester().withSecretKeySpec(aesSecret);
	}
	
	public AesCipher(byte[] aesSecret) {
		this(new String(aesSecret, UTF8));
	}

	public byte[] encrypt(byte[] clearText) {
		return digester.encriptAes128(clearText).unwrap();
	}

	public byte[] decrypt(byte[] encoded) {
		return digester.decriptAes128(encoded);
	}

}
