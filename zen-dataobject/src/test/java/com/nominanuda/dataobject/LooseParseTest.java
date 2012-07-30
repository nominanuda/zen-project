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
package com.nominanuda.dataobject;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.jws.Oneway;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.junit.BeforeClass;
import org.junit.Test;


public class LooseParseTest {
	private static String jsonString = "[{aaaa:1,b:'B',c:{}},false]";

	@Test
	public void testLexerStream() throws Exception {
		JsonLooseParser p = new JsonLooseParser();
		DataArray result = p.parseArray(jsonString);
		assertEquals(false, result.get(1));
		assertEquals("B", result.getObject(0).get("b"));
	}
}
