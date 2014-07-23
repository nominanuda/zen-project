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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;


public class AesTest {
//TODO
	@Test
	public void testAes128() throws InvalidKeyException, NoSuchAlgorithmException {
		//echo foo|openssl enc -aes128 -pass pass:jdjdsfjkdsjhfdjk -base64 -A
//		String cyphertext = "U2FsdGVkX1/Yqe4Lx+rKd1TIKuAvvgwcx8oNPMPBUVg=";
		Digester d = new Digester().withSecretKeySpec("jdjdsfjkdsjhfdjk");
//		byte[] encrypted = Base64.decodeNoGzip(cyphertext);
//		d.decriptAes128(encrypted);
		System.err.println(d.hmacSHA256("foo\n").toHex());
	}
}
