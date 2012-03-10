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
 * 
 */
package com.nominanuda.dataobject.jsonparser;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Exceptions;

public class JsonXmlReader implements XMLReader {
	private JSONParser parser = new JSONParser();
	private ContentHandler ch;

	public void setContentHandler(ContentHandler handler) {
		ch = handler;
	}

	public void parse(InputSource input) throws IOException, SAXException {
		try {
			DataStruct<?> ds = parser.parse(input.getCharacterStream());
			DataStructSAXStreamer.toSAX(ds, ch);
		} catch (ParseException e) {
			throw new IOException(Exceptions.toStackTrace(e));
		}
	}

	public void parse(String systemId) throws IOException, SAXException {
		parse(new InputSource(systemId));
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		throw new UnsupportedOperationException();
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		throw new UnsupportedOperationException();
	}

	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	public void setEntityResolver(EntityResolver resolver) {
		throw new UnsupportedOperationException();
	}

	public EntityResolver getEntityResolver() {
		throw new UnsupportedOperationException();
	}

	public void setDTDHandler(DTDHandler handler) {
	}

	public DTDHandler getDTDHandler() {
		throw new UnsupportedOperationException();
	}

	public ContentHandler getContentHandler() {
		throw new UnsupportedOperationException();
	}

	public void setErrorHandler(ErrorHandler handler) {
		throw new UnsupportedOperationException();
	}

	public ErrorHandler getErrorHandler() {
		throw new UnsupportedOperationException();
	}
}
