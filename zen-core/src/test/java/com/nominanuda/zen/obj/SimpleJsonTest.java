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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class SimpleJsonTest {

//TODO redo	@Test
//	public void testLooseParser() throws IOException {
//		String json = "{a:'la vispa',b:1.0 c:null, d [ pippo_n-e ]}";
//		DevNull<JixEvent> sink = new DevNull<JixEvent>();
//		SimpleJixParser.parse(new StringReader(json), JixHandler.adapt(sink));
//		assertEquals(12, sink.getEventCount());
//	}

	@Test
	public void testBuilder() throws IOException {
		String json = "{a:'la vispa',b:1.0 c:null, d [ pippo_n-e ]}";
		Obj o = SimpleJixParser.obj(json);
		assertEquals("la vispa", o.fetch("a"));
	}
}
