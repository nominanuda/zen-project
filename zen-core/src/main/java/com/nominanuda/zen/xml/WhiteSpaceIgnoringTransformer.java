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

import static com.nominanuda.zen.xml.Xml.XML;

import org.xml.sax.SAXException;



public class WhiteSpaceIgnoringTransformer extends ForwardingTransformerHandlerBase {

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(! XML.isWhiteSpace(ch, start, length)) {
			super.characters(ch, start, length);
		}
	}
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}
}
