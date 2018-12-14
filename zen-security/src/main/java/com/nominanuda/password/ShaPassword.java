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
package com.nominanuda.password;

import static com.nominanuda.zen.common.Str.STR;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.nominanuda.zen.codec.Hex;

public class ShaPassword extends SecurePassword {
	private final static String SEPARATOR = ":";
	public static enum Type {
		sha256, sha384, sha512;
		String algo() {
			switch (this) {
			case sha256:
				return "SHA-256";
			case sha384:
				return "SHA-384";
			case sha512:
				return "SHA-512";
			}
			return null;
		}
	}
	private final MessageDigest md;
	
	public ShaPassword(Type type) throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance(type.algo());
	}

	@Override
	public String hash(byte[] password) {
		return hash(password, salt());
	}
	
	@Override
	public boolean check(byte[] password, String hash) {
		String[] parts = hash.split(SEPARATOR);
		switch (parts.length) {
		case 1: // no salt
			return hash.equals(hash(password, new byte[0]));
		case 2: // with salt
			return hash.equals(hash(password, Hex.decode(parts[0])));
		}
		return false;
	}
	
	
	/* hashing */
	
	private String hash(byte[] password, byte[] salt) {
		if (salt.length > 0) { // we can salt
			byte[] message = new byte[salt.length + password.length];
			System.arraycopy(salt, 0, message, 0, salt.length);
			System.arraycopy(password, 0, message, salt.length, password.length);
			return STR.joinArgs(SEPARATOR, Hex.encode(salt), Hex.encode(md.digest(message)));
		}
		return Hex.encode(md.digest(password));
	}
}
