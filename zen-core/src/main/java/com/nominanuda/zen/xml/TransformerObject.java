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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;

public class TransformerObject extends Transformer {
	private static final SAXTransformerFactory txFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
	private Map<String, Object> params = new LinkedHashMap<String, Object>();
	private URIResolver uriResolver;
	private Properties outputProperties = new Properties();
	private ErrorListener errorListener;

	public void transform(Source xmlSource, Result outputTarget)
			throws TransformerException {
		Transformer t = txFactory.newTransformer();
		t.setErrorListener(errorListener);
		t.setOutputProperties(outputProperties);
		t.setURIResolver(uriResolver);
		for(Entry<String, Object> p : params.entrySet()) {
			t.setParameter(p.getKey(), p.getValue());
		}
		t.transform(xmlSource, outputTarget);
	}

	public void setParameter(String name, Object value) {
		params.put(name, value);
	}

	public Object getParameter(String name) {
		return params.get(name);
	}

	public void clearParameters() {
		params.clear();
	}

	public void setURIResolver(URIResolver resolver) {
		this.uriResolver = resolver;
	}

	public URIResolver getURIResolver() {
		return uriResolver;
	}

	public void setOutputProperties(Properties format) {
		this.outputProperties = format;
	}

	public Properties getOutputProperties() {
		return outputProperties;
	}

	public void setOutputProperty(String name, String value)
			throws IllegalArgumentException {
		outputProperties.put(name, value);
	}

	public String getOutputProperty(String name)
			throws IllegalArgumentException {
		return outputProperties.getProperty(name);
	}

	public void setErrorListener(ErrorListener listener)
			throws IllegalArgumentException {
		this.errorListener = listener;
	}

	public ErrorListener getErrorListener() {
		return errorListener;
	}
}
