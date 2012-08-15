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

import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.xml.SAXEmitter;
import com.nominanuda.xml.SaxBuffer;
import com.nominanuda.xml.SaxBuffer.SaxBit;

//TODO replace html in split blocks
public class HtmlSaxPage implements SAXEmitter {
	String htmlns = "http://www.w3.org/1999/xhtml";
	LinkedList<SaxBit> bits = new LinkedList<SaxBit>();

	public HtmlSaxPage() {
		bits.add(new SaxBuffer.StartElement(htmlns, "body", "body", new AttributesImpl()));
		bits.add(new SaxBuffer.EndElement(htmlns, "body", "body"));
	}

	public void applyStmt(DomManipulationStmt domManipulationStmt) {
		JQuerySaxMatcher m = new JQuerySaxMatcher(domManipulationStmt);
		for(int i = 0; i < bits.size();) {
			i = m.match(bits, i);
		}
	}

	public void toSAX(ContentHandler ch) throws SAXException {
		for(SaxBit b : bits) {
			b.send(ch);
		}
	}
}
