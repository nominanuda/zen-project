/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.codec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.nominanuda.zen.codec.Base62.B62;
import static com.nominanuda.zen.codec.Base64Codec.B64;
import static com.nominanuda.zen.common.Str.UTF8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class Digester {
	private static final String BLOWFISH = "Blowfish";
	private static final String SHA1 = "SHA1";
	private static final String AES = "AES";
	private static final String MD5 = "MD5";
	private static final String HMAC_SHA256 = "HmacSHA256";
	private SecretKeySpec aes128SecretKey, sha256SecretKey, blowfish128SecretKey;
	private Charset charset = UTF8; // default UTF8


	public static class Digest {
		private byte[] b;

		public Digest(byte[] digest) {
			b = digest;
		}
		public String toBase64Classic() {
			return B64.encodeClassic(b);
		}
		public String toBase64GzipClassic() {
			return B64.gzipEncodeClassic(b);
		}
		public String toBase64UrlSafeNoPad() {
			return B64.encodeUrlSafeNoPad(b);
		}
		public String toBase62() {
			return B62.encode(b);
		}
		public String toBase64GzipUrlSafeNoPad() {
			return B64.gzipEncodeUrlSafeNoPad(b);
		}
		public String toHex() {
			return Hex.encode(b);
		}
		public byte[] unwrap() {
			return b;
		}
	}


	public Digest hmacSHA256(String value) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(HMAC_SHA256);
		mac.init(sha256SecretKey);
		return new Digest(mac.doFinal(stringToBytes(value)));
	}

	public String hash(String seed, int nchars) {
		return sha1(seed).toBase64UrlSafeNoPad().substring(0, nchars);
	}
	public String hash(byte[] seed, int nchars) {
		return sha1(seed).toBase64UrlSafeNoPad().substring(0, nchars);
	}

	public Digest md5(String seed) {
		try {
			return md5(stringToBytes(seed));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public Digest md5(byte[] seed) {
		try {
			MessageDigest md = MessageDigest.getInstance(MD5);
			md.update(seed);
			return new Digest(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public Digest encriptAes128(byte[] clearMessage) {
		try {
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(ENCRYPT_MODE, aes128SecretKey);
			byte[] encrypted = cipher.doFinal(clearMessage);
			return new Digest(encrypted);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (IllegalBlockSizeException e) {
			throw new IllegalStateException(e);
		} catch (BadPaddingException e) {
			throw new IllegalStateException(e);
		}
	}
	public byte[] decriptAes128(byte[] encrypted) {
		try {
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(DECRYPT_MODE, aes128SecretKey);
			return cipher.doFinal(encrypted);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (IllegalBlockSizeException e) {
			throw new IllegalStateException(e);
		} catch (BadPaddingException e) {
			throw new IllegalStateException(e);
		}
	}
	public Digest encriptBlowfish128(byte[] clearMessage) {
		try {
			Cipher cipher = Cipher.getInstance(BLOWFISH);
			cipher.init(ENCRYPT_MODE, blowfish128SecretKey);
			byte[] encrypted = cipher.doFinal(clearMessage);
			return new Digest(encrypted);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (IllegalBlockSizeException e) {
			throw new IllegalStateException(e);
		} catch (BadPaddingException e) {
			throw new IllegalStateException(e);
		}
	}
	public byte[] decriptBlowfish128(byte[] encrypted) {
		try {
			Cipher cipher = Cipher.getInstance(BLOWFISH);
			cipher.init(DECRYPT_MODE, blowfish128SecretKey);
			return cipher.doFinal(encrypted);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (IllegalBlockSizeException e) {
			throw new IllegalStateException(e);
		} catch (BadPaddingException e) {
			throw new IllegalStateException(e);
		}
	}

	public Digest sha1(byte[] seed) {
		try {
			MessageDigest md = MessageDigest.getInstance(SHA1);
			md.update(seed);
			return new Digest(md.digest());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public Digest sha1(String seed) {
		try {
			return sha1(stringToBytes(seed));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private byte[] stringToBytes(String seed) {
		return seed.getBytes(charset);
	}

	public Digester withCharset(String cs) {
		setCharset(cs);
		return this;
	}
	public void setCharset(String cs) {
		charset = Charset.forName(cs);
	}

	public Digester withSecretKeySpec(String secretKey) {
		setSecretKeySpec(secretKey);
		return this;
	}
	public void setSecretKeySpec(String secretKey) {
		byte[] b = stringToBytes(secretKey);
		aes128SecretKey = new SecretKeySpec(Arrays.copyOf(b, 16), AES);
		sha256SecretKey = new SecretKeySpec(Arrays.copyOf(b, 32), HMAC_SHA256);
		blowfish128SecretKey = new SecretKeySpec(Arrays.copyOf(b, 16), BLOWFISH);
	}
}
