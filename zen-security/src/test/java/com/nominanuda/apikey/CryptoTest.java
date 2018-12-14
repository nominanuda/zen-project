package com.nominanuda.apikey;

import static com.nominanuda.zen.common.Str.UTF8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.security.KeyPair;

import org.junit.Test;

public class CryptoTest {

	@Test
	public void testCipher() {
		doTestCipher(new XorCipher());
		XorCipher c1 = new XorCipher();
		c1.setSecret("ladjklasjdlsajsadas");
		doTestCipher(c1);
		AesCipher aes = new AesCipher("rammorocumentosino");
		doTestCipher(aes);
	}

	private void doTestCipher(Cipher c) {
		byte[] msg = "somewhere over the rainbow èè".getBytes(UTF8);
		assertArrayEquals(msg, c.decrypt(c.encrypt(msg)));
	}

	@Test
	public void testSign() throws Exception {
		doTestSign(new Crc32Signer());
		KeyPair kp = Secp128r1Signer.genKeypair();
		String pri = Secp128r1Signer.base62OfPrivate(kp);
		String pub = Secp128r1Signer.base62OfPublic(kp);
		System.err.println("private:" + pri);
		System.err.println("public:" + pub);
		Secp128r1Signer s = new Secp128r1Signer();
		s.setPrivate(pri);
		s.setPublic(pub);
		doTestSign(s);
	}

	private void doTestSign(Signer c) {
		byte[] msg = "somewhere over the rainbow èè".getBytes(UTF8);
		byte[] sig = c.sign(msg);
		assertTrue(c.verify(msg, sig));
	}

}
