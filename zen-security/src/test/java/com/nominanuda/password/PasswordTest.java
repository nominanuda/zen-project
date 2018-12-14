package com.nominanuda.password;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.nominanuda.password.ShaPassword.Type;

public class PasswordTest {
	private final static int CYCLES = 100;
	private final static int PWD_LENGTH = 32;
	private List<SecurePassword> strategies = new ArrayList<SecurePassword>();
	
	@Before
	public void init() throws NoSuchAlgorithmException {
		strategies.clear();
		strategies.add(new ShaPassword(Type.sha256));
		strategies.add(new ShaPassword(Type.sha384));
		strategies.add(new ShaPassword(Type.sha512));
	}
	
	@Test
	public void test() {
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < CYCLES; i++) {
			checkPassword(new BigInteger(130, random).toString(PWD_LENGTH));
		}
//		checkPassword("pwd");
	}
	
	private void checkPassword(String password) {
		System.out.println(password);
		for (SecurePassword s : strategies) {
			String hash = s.hash(password);
			System.out.println(hash);
			Assert.assertTrue(s.check(password, hash));
		}
	}
	
	
	@Ignore @Test
	public void sha256pwd() throws NoSuchAlgorithmException {
		System.out.println(new ShaPassword(Type.sha256).hash("pwd".getBytes()));
	}
}
