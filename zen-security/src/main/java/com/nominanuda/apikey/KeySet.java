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

import javax.annotation.concurrent.Immutable;

import com.nominanuda.zen.obj.Obj;


@Immutable
public class KeySet implements Cipher, Signer {
	private static final String CIPHER = "cipher";
	private static final String DIGEST = "digest";
	private static final String KEY = "key";
	private static final String SIGN = "sign";
	private static final String VERIFY = "verify";
	
	
	private Cipher cipher;
	private Signer signer;
	
	private String ciph = XorCipher.XOR, key; // default xor, no key
	private String digest = Crc32Signer.CRC32, sign, verify; // default crc32
	
	
	public KeySet() {
		// default
	}
	public KeySet(Obj conf) {
		setCipher(conf.getStr(CIPHER));
		setDigest(conf.getStr(DIGEST));
		setKey(conf.getStr(KEY));
		setSign(conf.getStr(SIGN));
		setVerify(conf.getStr(VERIFY));
	}
	
	
	public byte[] encrypt(byte[] clearText) {
		return cipher.encrypt(clearText);
	}
	
	public byte[] decrypt(byte[] cypherText) {
		return cipher.decrypt(cypherText);
	}
	
	public byte[] sign(byte[] msg) {
		return signer.sign(msg);
	}
	
	public boolean verify(byte[] msg, byte[] signature) {
		return signer.verify(msg, signature);
	}
	
	
	public void init() throws Exception {
		if (ciph != null) {
			switch (ciph) {
			case XorCipher.XOR:
				XorCipher c = new XorCipher();
				if (key != null) c.setSecret(key);
				this.cipher = c;
				break;
			case AesCipher.AES:
				this.cipher = new AesCipher(key);
				break;
			default:
				throw new IllegalArgumentException("unknown algo: " + ciph);
			}
		}
		if (digest != null) {
			switch (digest) {
			case Crc32Signer.CRC32:
				this.signer = new Crc32Signer();
				break;
			case Secp128r1Signer.SECP128R1:
				Secp128r1Signer s = new Secp128r1Signer();
				if (sign != null) s.setPrivate(sign);
				if (verify != null) s.setPublic(verify);
				this.signer = s;
				break;
			default:
				throw new IllegalArgumentException("unknown algo: " + digest);
			}
		}
	}
	
	
	
	/* for spring config */
	
	public void setCipher(String ciph) {
		if (ciph != null) { // avoid clearing default
			this.ciph = ciph;
		}
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setDigest(String digest) {
		if (digest != null) { // avoid clearing default
			this.digest = digest;
		}
	}
	public void setSign(String sign) {
		if (sign != null) {
			digest = Secp128r1Signer.SECP128R1; // default in case of sign/verify
		}
		this.sign = sign;
	}
	public void setVerify(String verify) {
		if (verify != null) {
			digest = Secp128r1Signer.SECP128R1; // default in case of sign/verify
		}
		this.verify = verify;
	}
	
	
	
	/* utils */
	
	public static KeySet nullOne() {
		return new KeySet() {
			@Override
			public byte[] encrypt(byte[] clearText) {
				throw new SecurityException("missing rights");
			}

			@Override
			public byte[] decrypt(byte[] cypherText) {
				throw new SecurityException("missing rights");
			}

			@Override
			public byte[] sign(byte[] msg) {
				throw new SecurityException("missing rights");
			}

			@Override
			public boolean verify(byte[] msg, byte[] signature) {
				throw new SecurityException("missing rights");
			}
		};
	}
}
