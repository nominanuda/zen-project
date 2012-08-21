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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.nominanuda.xml.RootStripTransformer;

import static com.nominanuda.xml.XmlHelper.XML;
import static com.nominanuda.io.IOHelper.IO;

public class FragmentSaxParser implements XMLReader {
	private static final String startTag = "<XXXXXX>";
	private static final byte[] startTagBuf = startTag.getBytes();
	private static final String endTag = "</XXXXXX>";
	private static final byte[] endTagBuf = endTag.getBytes();
	private RootStripTransformer contentHandlerWrapper;
	private ContentHandler wrappedHandler;

	private final XMLReader delegee;

	public FragmentSaxParser() throws SAXException {
		delegee = XML.newParser().getXMLReader();
	}
	public FragmentSaxParser(XMLReader delegee) {
		this.delegee = delegee;
	}

	public void parse(InputSource input) throws IOException, SAXException {
		if(input.getByteStream() != null) {
			delegee.parse(new InputSource(IO.concat(
				new ByteArrayInputStream(startTagBuf),
				input.getByteStream(),
				new ByteArrayInputStream(endTagBuf))));
		} else if(input.getCharacterStream() != null) {
			delegee.parse(new InputSource(IO.concat(
				new StringReader(startTag),
				input.getCharacterStream(),
				new StringReader(endTag))));
		} else {
			String systemId = input.getSystemId();
			URL url = new URL(systemId);
			InputStream is = url.openStream();
			delegee.parse(new InputSource(IO.concat(
				new ByteArrayInputStream(startTagBuf),
				is,
				new ByteArrayInputStream(endTagBuf))));
		}
	}

	public void parse(String systemId) throws IOException, SAXException {
		parse(new InputSource(systemId));
	}

	public void setContentHandler(ContentHandler handler) {
		contentHandlerWrapper = new RootStripTransformer();
		contentHandlerWrapper.setResult(new SAXResult(handler));
		delegee.setContentHandler(contentHandlerWrapper);
		wrappedHandler = handler;
	}

	public ContentHandler getContentHandler() {
		return wrappedHandler;
	}

	public DTDHandler getDTDHandler() {
		return delegee.getDTDHandler();
	}

	public EntityResolver getEntityResolver() {
		return delegee.getEntityResolver();
	}

	public ErrorHandler getErrorHandler() {
		return delegee.getErrorHandler();
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return delegee.getFeature(name);
	}

	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return delegee.getProperty(name);
	}

	public void setDTDHandler(DTDHandler handler) {
		delegee.setDTDHandler(handler);
	}

	public void setEntityResolver(EntityResolver resolver) {
		delegee.setEntityResolver(resolver);
	}

	public void setErrorHandler(ErrorHandler handler) {
		delegee.setErrorHandler(handler);
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		delegee.setFeature(name, value);
	}

	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		delegee.setProperty(name, value);
	}


}
