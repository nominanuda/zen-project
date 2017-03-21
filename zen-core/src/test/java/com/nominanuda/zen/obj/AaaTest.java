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
package com.nominanuda.zen.obj;

import static com.nominanuda.zen.common.Str.UTF8;
import static org.junit.Assert.*;

import org.junit.Test;

import com.nominanuda.zen.common.Str;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.ObjImpl;
import com.nominanuda.zen.obj.Stru;

public class AaaTest {


	@Test
	public void test3() {
		Obj o = new ObjImpl();
		o = Obj.make();
		o.newObj();
//		o.set("foo", "bar");
//		assertEquals("bar", o.get("foo"));
	}

	@Test
	public void test2() {
		//fail("Not yet implemented");
		String x  = "mnfdjhfkajhdfhladhjfkafdhfdhjjdsjhadslfe";//é
		for(byte b : x.getBytes(UTF8)) {
			assertTrue(isAsciiPrintable(b));
		}
		for(byte b : "é°".getBytes(UTF8)) {
			assertFalse(isAsciiPrintable(b));
		}
		assertEquals(x.hashCode(), hashCode(x.toCharArray()));
		assertEquals(x.hashCode(), hashCodeBytes(x.getBytes(UTF8)));
		assertEquals(x.length(), x.getBytes(UTF8).length);
	}

	public int hashCode(char[] value) {
		int h = 0;
		if (value.length > 0) {
			for (int i = 0; i < value.length; i++) {
				h = 31 * h + value[i];
			}
		}
		return h;
	}

	public int hashCodeBytes(byte[] value) {
		int h = 0;
		if (value.length > 0) {
			for (int i = 0; i < value.length; i++) {
				h = 31 * h + value[i];
			}
		}
		return h;
	}
	boolean isAsciiPrintable(byte b) {
		byte _10000000 = (byte)0b10000000;
		byte _11100000 = (byte)0b11100000;
		byte _0 = 0b00000000;
		if((b & _10000000) == _0) {
			if((b & _11100000) == _0) {
				return false;
			}
			return true;
		}
		return false;
	}
	//32 126

}
