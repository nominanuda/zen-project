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

import static com.nominanuda.zen.common.Str.STR;
import static com.nominanuda.zen.common.Str.UTF8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class AnyUnmarshallerTest {

	@Test
	public void testUnmarshalString() {
		assertNull(unmarshal("null").asVal().get());
		assertTrue((Boolean)unmarshal("true").asVal().get());
		assertFalse((Boolean)unmarshal("false").asVal().get());
		String s = "la vispà";
		Any res = unmarshal("\""+s+"\"");
		assertEquals(s, res.asVal().get());
		assertEquals(1.2d, unmarshal("1.2").asVal().get());
		assertEquals(12, unmarshal("012").asVal().get());
	}

	@Test
	public void testUnmarshalEscapedString() {
		Any res = unmarshal("{\"title\":\"èèèèüüü \\\"Jauchzet,  Tage\\\"  \"}");
		assertEquals("èèèèüüü \"Jauchzet,  Tage\"  ", res.asObj().getStr("title"));
		
		System.err.println(res.toString());
	}

	@Test
	public void testUnmarshalNegativeNumber() {
		Any res = unmarshal("{\"x\": -2}");
		assertEquals(-2, res.asObj().getInt("x").intValue());
		try {
			unmarshal("{\"x\": 2-2}");
			Assert.fail();
		} catch(IllegalArgumentException e) {}
		System.err.println(res.toString());
	}

	private Any unmarshal(String s) {
		ByteBuffer bb = ByteBuffer.wrap(s.getBytes(UTF8));
		AnyNioDeserializer parser = new AnyNioDeserializer();
		parser.onNext(bb);
		parser.onComplete();
		Any res = parser.get();
		return res;
	}
	

	@Test
	public void test1() {
		int nrounds = 1000;
		int nobjs = 1000;
		final int bufSize = 1024;
		/////
		String obj = "{\"a\":\"A\",\"b\":1,\"c\":false,\"d\":null}";
		String arr = "[" + STR.ntimes(obj+",", nobjs-1)+obj+"]";
		byte[] msg = arr.getBytes(UTF8);
		int msgLen = msg.length;
		List<ByteBuffer> bbs = toNio(msg, bufSize);
		long start = System.currentTimeMillis();
		for(int i = 0; i < nrounds; i++) {
			AnyNioDeserializer parser = new AnyNioDeserializer();
			for(int j = 0; j < bbs.size(); j++) {
				parser.onNext(bbs.get(j));
			}
			parser.onComplete();
			Any res = parser.get();
			assertNotNull(res);
			assertTrue(res instanceof Arr);
			assertEquals(nobjs, ((Arr)res).len());
		}
		long tt = System.currentTimeMillis() - start;
		System.err.println(String.format(
			"AnyUnmarshallerTest:[nrounds %d - msgLen %d - bufSize %d - nBufs %d - totTime msec %d - parseTime msec %.2f - byte/sec %d]"
			,nrounds, msgLen, bufSize, bbs.size(), tt, (double)tt/nrounds, msgLen * nrounds / tt * 1000
		));
	}
	private List<ByteBuffer> toNio(byte[] msg, int bufSize) {
		List<ByteBuffer> res = new LinkedList<>();
		int i;
		for(i = 0; i + bufSize < msg.length; i += bufSize) {
			ByteBuffer b = ByteBuffer.wrap(Arrays.copyOfRange(msg, i, i + bufSize));
			res.add(b);
		}
		ByteBuffer b = ByteBuffer.wrap(Arrays.copyOfRange(msg, i, msg.length));
		res.add(b);
		return res;
	}
}
