/*
 * Copyright 2008-2011 the original author or authors.
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
package com.nominanuda.lang;

import java.math.BigInteger;
import java.util.UUID;

import com.nominanuda.code.ThreadSafe;

@ThreadSafe
public abstract class Maths {
	private static final BufferHelper bufs = new BufferHelper();

	public static boolean isInteger(Number n) {
		return n.equals(Math.floor(n.doubleValue()));
	}

	public static boolean isInteger(String str) {
		Check.notNull(str);
		try {
			Long.parseLong(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static  byte[] getBytes(long n) {
		byte[] b0 = new BigInteger(Long.toHexString(n), 16).toByteArray();
		byte[] b1 = new byte[b0.length - 1]; // b0 without the sign bit
		System.arraycopy(b0, 1, b1, 0, b1.length);
		return b1;
	}

	public static  byte[] getBytes(UUID uuid) {
		byte[] b1 = getBytes(uuid.getMostSignificantBits());
		byte[] b2 = getBytes(uuid.getLeastSignificantBits());
		return bufs.concat(b1, b2);
	}

	public static String toString(Number o) {
		return isInteger((Number)o)
			? new Long(((Number)o).longValue()).toString()
			: o.toString();
	}

	public static boolean isNumber(String n) {
		try {
			Double.valueOf(n);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static int asUnsignedByte(byte b) {
		return b < 0
			? (int) (b + 256)
			: (int) b;
	}
}
