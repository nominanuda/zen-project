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
package com.nominanuda.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.lang.ReflectiveObjectFactory;
import com.nominanuda.lang.Strings;

public class IdentityPipeTest {

	@Test
	public void testIdentity() throws Exception {
		String xmlmsg = "<a>lavispateresa</a>";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter ow = new OutputStreamWriter(baos, Strings.UTF8);
		new SAXPipeline()
			.addXslt(new StringReader(XmlHelper.identityXslt))
			.add(new ReflectiveObjectFactory<TransformerHandler>(RootStripTransformer.class))
			.add(new ReflectiveObjectFactory<TransformerHandler>(WhiteSpaceIgnoringTransformer.class))
			.complete()
			.build(new StreamSource(new StringReader(xmlmsg)), new SAXResult(new XmlSerializer(ow)))
			.run();
		ow.flush();
		Assert.assertEquals("lavispateresa", new String(baos.toByteArray(), "UTF-8"));
	}
}
