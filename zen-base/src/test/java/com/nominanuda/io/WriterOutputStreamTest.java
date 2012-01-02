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
package com.nominanuda.io;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;


public class WriterOutputStreamTest {

	@Test
	public void testWriterOutputStream() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter w = new OutputStreamWriter(baos);
		WriterOutputStream wos = new WriterOutputStream(w);
		byte[] msg = "là vispà".getBytes("UTF-8");
		wos.write(msg,0,2);
		wos.write(msg,2,msg.length-3);
		wos.write(msg,msg.length-1, 1);
		wos.close();
		System.err.println(new String(baos.toByteArray(),"UTF-8"));
		Assert.assertArrayEquals(msg, baos.toByteArray());
	}
}
