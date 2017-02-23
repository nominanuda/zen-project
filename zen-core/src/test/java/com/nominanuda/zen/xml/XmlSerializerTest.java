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
package com.nominanuda.zen.xml;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.nominanuda.zen.xml.FragmentSaxParser;
import com.nominanuda.zen.xml.XmlSerializer;

public class XmlSerializerTest {

	@Test
	public void test() throws Exception {
		String s = "<z:a xmlns:z=\"http://foo.com\"/><a xmlns:unused=\"ftp:unused\"/><c xmlns=\"http://foo2.com\" xmlns:z=\"http://foo.com\"><z:D z:attr=\"1 or 2\"/></c>";
		StringWriter sw = new StringWriter();
		XmlSerializer ser = new XmlSerializer();
		ser.setWriter(sw);
		XMLReader r = new FragmentSaxParser();
		r.setContentHandler(ser);
		r.parse(new InputSource(new StringReader(s)));
		Assert.assertEquals(s, sw.toString());
	}

}
