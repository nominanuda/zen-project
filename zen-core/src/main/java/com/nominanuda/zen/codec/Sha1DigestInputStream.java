package com.nominanuda.zen.codec;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1DigestInputStream extends DigestInputStream {

	public Sha1DigestInputStream(InputStream stream) throws NoSuchAlgorithmException {
		super(stream, MessageDigest.getInstance("SHA1"));
	}
	
	public String hex() {
		return Hex.encode(getMessageDigest().digest());
	}
}
