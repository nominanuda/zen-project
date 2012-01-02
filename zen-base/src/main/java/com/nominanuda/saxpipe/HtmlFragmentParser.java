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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import nu.validator.htmlparser.sax.HtmlParser;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class HtmlFragmentParser implements XMLReader {
		private HtmlParser parser;

		public HtmlFragmentParser(HtmlParser htmlParser) {
			this.parser = htmlParser;
		}

		public void parse(InputSource input) throws IOException, SAXException {
			parser.parseFragment(input, "div");
		}

		public void parse(String systemId) throws IOException, SAXException {
			InputStream is = new URL(systemId).openStream();
			InputSource input = new InputSource(is);
			parse(input);
		}

		public boolean getFeature(String name)
				throws SAXNotRecognizedException, SAXNotSupportedException {
			return parser.getFeature(name);
		}

		public void setFeature(String name, boolean value)
				throws SAXNotRecognizedException, SAXNotSupportedException {
			parser.setFeature(name, value);
		}

		public Object getProperty(String name)
				throws SAXNotRecognizedException, SAXNotSupportedException {
			return parser.getProperty(name);
		}

		public void setProperty(String name, Object value)
				throws SAXNotRecognizedException, SAXNotSupportedException {
			parser.setProperty(name, value);
		}

		public void setEntityResolver(EntityResolver resolver) {
			parser.setEntityResolver(resolver);
		}

		public EntityResolver getEntityResolver() {
			return parser.getEntityResolver();
		}

		public void setDTDHandler(DTDHandler handler) {
//			parser.setDTDHandler(handler);
		}

		public DTDHandler getDTDHandler() {
			return parser.getDTDHandler();
		}

		public void setContentHandler(ContentHandler handler) {
			parser.setContentHandler(handler);
			parser.setLexicalHandler((LexicalHandler)handler);
		}

		public ContentHandler getContentHandler() {
			return parser.getContentHandler();
		}

		public void setErrorHandler(ErrorHandler handler) {
			parser.setErrorHandler(handler);
		}

		public ErrorHandler getErrorHandler() {
			return parser.getErrorHandler();
		}
	}