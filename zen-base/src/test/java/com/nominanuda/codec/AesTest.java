package com.nominanuda.codec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;


public class AesTest {

	@Test
	public void testAes128() throws InvalidKeyException, NoSuchAlgorithmException {
		//echo foo|openssl enc -aes128 -pass pass:jdjdsfjkdsjhfdjk -base64 -A
		String cyphertext = "U2FsdGVkX1/Yqe4Lx+rKd1TIKuAvvgwcx8oNPMPBUVg=";
		Digester d = new Digester().withSecretKeySpec("jdjdsfjkdsjhfdjk");
//		byte[] encrypted = Base64.decodeNoGzip(cyphertext);
//		d.decriptAes128(encrypted);
		System.err.println(d.hmacSHA256("foo\n").toHex());
	}
}
