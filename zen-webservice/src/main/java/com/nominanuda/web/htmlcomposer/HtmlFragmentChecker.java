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
package com.nominanuda.web.htmlcomposer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.xml.ForwardingTransformerHandlerBase;

public class HtmlFragmentChecker extends ForwardingTransformerHandlerBase implements HttpProtocol {

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if(! HTMLNS.equals(uri)) {
			uri = HTMLNS;
		}
		super.startElement(uri, localName, qName, atts);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(! HTMLNS.equals(uri)) {
			uri = HTMLNS;
		}
		super.endElement(uri, localName, qName);
	}

}
