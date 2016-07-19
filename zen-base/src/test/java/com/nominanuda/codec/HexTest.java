package com.nominanuda.codec;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class HexTest {
	private final static int RANDOMS = 4096;
	
	@Test
	public void test() throws NoSuchAlgorithmException {
		byte [] bytes = new byte[16];
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < RANDOMS; i++) {
			r.nextBytes(bytes);
			String str = Hex.encode(bytes);
			Assert.assertArrayEquals(Hex.decode(str), bytes);
		}
	}
}
