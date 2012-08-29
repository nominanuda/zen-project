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

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import com.nominanuda.lang.Check;
import com.nominanuda.lang.ObjectFactory;

public class SAXPipeline {
	private SAXTransformerFactory txFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
	private Properties outputProperties = new Properties();
	private List<Object> components = new LinkedList<Object>();
	private boolean completed = false;

	public SAXPipeline addXslt(Reader xsl) throws TransformerConfigurationException {
		Check.illegalstate.assertFalse(completed);
		components.add(txFactory.newTemplates(new StreamSource(xsl)));
		return this;
	}
	public SAXPipeline addXslt(InputStream xsl) throws TransformerConfigurationException {
		Check.illegalstate.assertFalse(completed);
		components.add(txFactory.newTemplates(new StreamSource(xsl)));
		return this;
	}
	public SAXPipeline addXslt(String xslURI) throws TransformerConfigurationException {
		Check.illegalstate.assertFalse(completed);
		components.add(txFactory.newTemplates(new StreamSource(xslURI)));
		return this;
	}
	public SAXPipeline add(ObjectFactory<? extends TransformerHandler> transformer) {
		Check.illegalstate.assertFalse(completed);
		components.add(transformer);
		return this;
	}
	public SAXPipeline addOutputProperty(String name, String value) {
		Check.illegalstate.assertFalse(completed);
		outputProperties.put(name, value);
		return this;
	}
	public SAXPipeline setOutputProperties(Properties p) {
		Check.illegalstate.assertFalse(completed);
		Check.notNull(p);
		outputProperties = p;
		return this;
	}
	public SAXPipeline setComponents(List<?> components) {
		Check.illegalstate.assertFalse(completed);
		this.components.clear();
		for(Object c : components) {
			this.components.add(c);
		}
		return this;
	}
	public SAXPipeline complete() {
		Check.illegalstate.assertFalse(completed);
		completed = true;
//		Check.illegalstate.assertFalse(components.isEmpty());
		Collections.reverse(components);
		if(outputProperties.isEmpty()) {//TODO default ?? remove
			outputProperties.put(OutputKeys.INDENT, "yes");
			outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		return this;
	}
	public Runnable build(final Source source, final Result result) {
		if(! completed) {
			complete();
		}
		return new Runnable() {
			public void run() {
				try {
					txFactory.newTransformer().transform(source, build(result));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	public Runnable build(final SAXEmitter emitter, final SAXResult result) {
		if(! completed) {
			complete();
		}
		return new Runnable() {
			public void run() {
				try {
					emitter.toSAX(((SAXResult)build(result)).getHandler());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		};
	}

	private TransformerHandler buildTranformerHandler(Object c) throws TransformerConfigurationException {
		Check.illegalstate.assertTrue(completed);
		if(c instanceof ObjectFactory) {
			return (TransformerHandler)((ObjectFactory<?>)c).getObject();
		} else if(c instanceof Templates) {
			return txFactory.newTransformerHandler((Templates)c);
		} else {
			return Check.illegalstate.fail();
		}
	}

	private Result build(final Result result) throws TransformerConfigurationException {
		Check.illegalstate.assertTrue(completed);
		Result nextRes = result;
		Iterator<Object> itr = components.iterator();
		boolean first = true;
		while (itr.hasNext()) {
			Object c = itr.next();
			TransformerHandler th = buildTranformerHandler(c);
			if(first) {
				first = false;
				th.getTransformer().setOutputProperties(outputProperties);
			}
			th.setResult(nextRes);
			nextRes = new SAXResult(th);
		}
		return 
		//(SAXResult)
		nextRes;
	}
}
