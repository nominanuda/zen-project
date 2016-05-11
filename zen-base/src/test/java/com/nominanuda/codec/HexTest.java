package com.nominanuda.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

public class HexTest {
	private final static String[] STRINGS = new String[] {};
	private final static int RANDOMS = 4096;
	
	@Test @Ignore
	public void test() throws NoSuchAlgorithmException {
		for (String s : STRINGS) {
			doHex(s.getBytes());
		}
		
		byte [] bytes = new byte[16];
		Random r = new Random(System.currentTimeMillis());
		MessageDigest md = MessageDigest.getInstance("MD5");
		for (int i = 0; i < RANDOMS; i++) {
			r.nextBytes(bytes);
			doHex(md.digest(bytes));
		}
	}
	
	private void doHex(byte[] bytes) {
		String zenHex = Hex.encode(bytes);
//		Assert.assertEquals(Hex.encodeAlternative(bytes), zenHex);
		System.out.println(zenHex);
	}
}
