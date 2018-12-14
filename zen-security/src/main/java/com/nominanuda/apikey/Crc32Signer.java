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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Crc32Signer implements Signer {

	public static final String CRC32 = "crc32";

	@Override
	public byte[] sign(byte[] msg) {
		CRC32 crc = new CRC32();
		crc.update(msg);
		return toBytes(new Long(crc.getValue()).intValue());
	}

	private byte[] toBytes(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	@Override
	public boolean verify(byte[] msg, byte[] signature) {
		byte[] sig2 = sign(msg);
		boolean verified = Arrays.equals(signature, sig2);
		return verified;
	}

}
