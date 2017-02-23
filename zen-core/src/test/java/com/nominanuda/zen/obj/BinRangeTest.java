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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

import java.nio.CharBuffer;

import org.junit.Test;

import com.nominanuda.zen.obj.BinRange;
import com.nominanuda.zen.stereotype.Value;

public class BinRangeTest {

	@Test
	public void test() {
		ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
		String[] illegal = {
			"TRUE", "False" , " null", "1..0", ":", "a", " ", ""
		};
		for(int i = 0; i < illegal.length; i++) {
			String json = illegal[i];
			CharBuffer cb = CharBuffer.wrap(json);
			ByteBuf bb =  ByteBufUtil.encodeString(alloc, cb, UTF_8);
			BinRange r  = new BinRange(bb);
			try {
				r.decode();
				fail();
			} catch(IllegalArgumentException e) {
			}
		}
		Object[] jsonJava = {
			"true", Boolean.TRUE,
			"false", Boolean.FALSE,
			"null", null,
			"1", 1,
			"-0", 0,
			".0", 0d,
			"1.0",1d,
		};
		for(int i = 0; i < jsonJava.length/2; i++) {
			String json = (String)jsonJava[2*i];
			Object java = jsonJava[2*i+1];
			CharBuffer cb = CharBuffer.wrap(json);
			ByteBuf bb =  ByteBufUtil.encodeString(alloc, cb, UTF_8);
			BinRange r  = new BinRange(bb);
			assertTrue(Value.nullSafeEquals(java, r.decode()));
		}
	}

	@Test
	public void testString() {
		ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
		String[] illegal = {
			"\"", "\\uWOW" , "\\u123 "
		};
		for(int i = 0; i < illegal.length; i++) {
			String json = "\""+illegal[i]+"\"";
			CharBuffer cb = CharBuffer.wrap(json);
			ByteBuf bb =  ByteBufUtil.encodeString(alloc, cb, UTF_8);
			BinRange r  = new BinRange(bb);
			try {
				r.decode();
				fail();
			} catch(IllegalArgumentException e) {
			}
		}
		String[] jsonJava = {
			"agoráà", "agoráà",
			"\\\"", "\"",
			"/", "/",
			"\\u1234", "ሴ",
			"\\//\\/", "///",
			"  \\\\u1234", "  \\u1234",
			"", "",
		};
		for(int i = 0; i < jsonJava.length/2; i++) {
			String json = "\""+jsonJava[2*i]+"\"";
			String java = jsonJava[2*i+1];
			CharBuffer cb = CharBuffer.wrap(json);
			ByteBuf bb =  ByteBufUtil.encodeString(alloc, cb, UTF_8);
			BinRange r  = new BinRange(bb);
			assertEquals(java, r.decode());
		}
	}

}
