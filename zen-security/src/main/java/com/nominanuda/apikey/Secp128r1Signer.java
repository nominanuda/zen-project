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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static com.nominanuda.zen.codec.Base62.B62;

public class Secp128r1Signer implements Signer {
	public static final String SECP128R1 = "secp128r1";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	@Override
	public byte[] sign(byte[] clearText) {
		Signature ecdsaSigner;
		try {
			ecdsaSigner = Signature.getInstance("ECDSA", "BC");
			ecdsaSigner.initSign(getPrivateKey());
			ecdsaSigner.update(clearText);
			byte[] sig = ecdsaSigner.sign();
			return sig;
		} catch (Exception e) {
			throw new SecurityException(e);
		}
	}

	PrivateKey pri;
	private PrivateKey getPrivateKey() {
		return pri;
	}
	public void setPrivateKey(PrivateKey pri) throws Exception {
		this.pri = pri;
	}
	public void setPrivate(String b62Pri) throws Exception {
		byte[] bb = B62.decode(b62Pri);
		KeyFactory kFact = KeyFactory.getInstance("EC", "BC");
		PrivateKey _pri = kFact.generatePrivate(new PKCS8EncodedKeySpec(
				PrivateKeyInfo.getInstance(bb).getEncoded()));
		setPrivateKey(_pri);
	}

	@Override
	public boolean verify(byte[] clearText, byte[] signature) {
		Signature ecdsaSigner;
		try {
			ecdsaSigner = Signature.getInstance("ECDSA", "BC");
			ecdsaSigner.initVerify(getPublicKey());
			ecdsaSigner.update(clearText);
			return ecdsaSigner.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	PublicKey pub;
	private PublicKey getPublicKey() {
		return pub;
	}
	public void setPublicKey(PublicKey pub) {
		this.pub = pub;
	}
	public void setPublic(String b62Pub) throws Exception {
		byte[] bb = B62.decode(b62Pub);
		KeyFactory kFact = KeyFactory.getInstance("EC", "BC");
		PublicKey _pub = kFact.generatePublic(new X509EncodedKeySpec(bb));
		setPublicKey(_pub);
	}
	public static KeyPair genKeypair() throws Exception {
//		X9ECParameters x9Params = ECUtil.getNamedCurveByOid(ECUtil
//				.getNamedCurveOid("secp256r1"));
//		if (x9Params.getCurve() instanceof ECCurve.Fp) {
//		fail("curve not custom curve!!");
//		}		
		AlgorithmParameterSpec ecSpec = ECNamedCurveTable
				.getParameterSpec(SECP128R1);
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("EC", "BC");
		keygen.initialize(ecSpec, new /*ECRandom*/SecureRandom());
		
		KeyPair keys = keygen.generateKeyPair();
		return keys;
	}
	public static String base62OfPublic(KeyPair kp) throws Exception {
		return base62OfPublic(kp.getPublic());
	}
	public static String base62OfPublic(PublicKey kp) throws Exception {
		return B62.encode(kp.getEncoded());
	}
	public static String base62OfPrivate(KeyPair kp) throws Exception {
		return base62OfPrivate(kp.getPrivate());
	}
	public static String base62OfPrivate(PrivateKey kp) throws Exception {
		return B62.encode(kp.getEncoded());
	}

	@SuppressWarnings("unused")
	private static class ECRandom extends SecureRandom {
		private static final long serialVersionUID = -7028253748539314167L;
		public void nextBytes(byte[] bytes) {
			byte[] src = new BigInteger("e2eb6663f551331bda00b90f1272c09d980260c1a70cab1ec481f6c937f34b62", 16).toByteArray();
			if (src.length <= bytes.length) {
				System.arraycopy(src, 0, bytes, bytes.length - src.length,
						src.length);
			} else {
				System.arraycopy(src, 0, bytes, 0, bytes.length);
			}
		}
	}
}
