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
package com.nominanuda.saxpipe;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.lang.ReflectiveObjectFactory;
import com.nominanuda.saxpipe.SAXHelper;
import com.nominanuda.saxpipe.SAXPipeline;
import com.nominanuda.saxpipe.WhiteSpaceIgnoringTransformer;

public class IdentityPipeTest {

	@Test
	public void testIdentity() throws Exception {
		String xmlmsg = "<a>lavispateresa</a>\n";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new SAXPipeline()
			.add(new ReflectiveObjectFactory<TransformerHandler>(WhiteSpaceIgnoringTransformer.class))
			.addXslt(new StringReader(SAXHelper.identityXslt))
			.add(new ReflectiveObjectFactory<TransformerHandler>(WhiteSpaceIgnoringTransformer.class))
//TODO...			.add(new ReflectiveObjectFactory<TransformerHandler>(RootStripTransformer.class))
			.addXslt(new StringReader(SAXHelper.identityXslt))
			.complete()
			.build(new StreamSource(new StringReader(xmlmsg)), new StreamResult(baos))
			.run();
		Assert.assertEquals(xmlmsg, new String(baos.toByteArray(), "UTF-8"));
	}
}
