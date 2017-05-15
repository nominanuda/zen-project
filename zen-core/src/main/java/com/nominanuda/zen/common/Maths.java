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
package com.nominanuda.zen.common;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

@ThreadSafe
public class Maths {
	public static final Maths MATHS = ScopedSingletonFactory.getInstance().buildJvmSingleton(Maths.class);

	public boolean isInteger(Number n) {
		return n.equals(Math.floor(n.doubleValue()));
	}

	public boolean isInteger(String str) {
		try {
			Long.parseLong(Check.notNull(str));
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public byte[] getBytes(long n) {
		return new BigInteger(Long.toHexString(n), 16).toByteArray();
	}

	public byte[] getBytes(UUID uuid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	public UUID getUUID(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		long high = bb.getLong();
		long low = bb.getLong();
		UUID uuid = new UUID(high, low);
		return uuid;
	}

	private byte[] concat(byte[]...bs) {
		int len = 0;
		for(int i = 0; i < bs.length; i++) {
			len += bs[i].length;
		}
		byte[] res = new byte[len];
		int cur = 0;
		for(int i = 0; i < bs.length; i++) {
			int length = bs[i].length;
			System.arraycopy(bs[i], 0, res, cur, length);
			cur += length;
		}
		return res;
	}
	public String toString(Number o) {
		return isInteger((Number)o)
			? new Long(((Number)o).longValue()).toString()
			: o.toString();
	}

	public boolean isNumber(String n) {
		try {
			Double.valueOf(n);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public int asUnsignedByte(byte b) {
		return b < 0
			? (int) (b + 256)
			: (int) b;
	}

	public long randLong(long max) {
		return new Double(Math.random() * max).longValue();
	}

	public int randInt(int max) {
		return (int)randLong(max);
	}
}
