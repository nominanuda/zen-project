package com.nominanuda.zen.codec;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5DigestInputStream extends DigestInputStream {
	public Md5DigestInputStream(InputStream stream) throws NoSuchAlgorithmException {
		super(stream, (MessageDigest.getInstance("MD5")));
	}

	public String hex() {
		return Hex.encode(getMessageDigest().digest());
	}
}
