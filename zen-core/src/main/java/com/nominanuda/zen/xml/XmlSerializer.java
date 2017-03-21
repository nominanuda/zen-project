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

import static com.nominanuda.zen.common.Str.STR;
import static com.nominanuda.zen.xml.Xml.XML;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import com.nominanuda.zen.common.Check;

public class XmlSerializer implements ContentHandler, LexicalHandler {

	private boolean forrestMode = true;
	private boolean stripComments = false;
	//private boolean omitXmlDecl = true;
	private boolean oneline = false;
	//private boolean pretty = false;
	private boolean xmlEscapeAposAndQuote = false;

	private boolean docStarted = false;
	private boolean docEnded = false;

	private SAXThrowingWriter w;
	private boolean withinCdata = false;

	private Map<String, String> prefixMappings = new HashMap<String, String>();
	private Map<String, String> pendingPrefixMappings = new LinkedHashMap<String, String>();
	private boolean startTagJustSeen = false;

	public XmlSerializer() {
	}

	public XmlSerializer(Writer w) {
		setWriter(w);
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		checkInDocFlow();
		pendingPrefixMappings.put(prefix, uri);
		prefixMappings.put(prefix, uri);
	}
	public void endPrefixMapping(String prefix) throws SAXException {
		checkInDocFlow();
		pendingPrefixMappings.remove(prefix);
		prefixMappings.remove(prefix);
	}

	public void startDocument() throws SAXException {
		assertFalse(docStarted);
		docStarted = true;
	}

	public void endDocument() throws SAXException {
		assertTrue(docStarted);
		docEnded = true;
	}


	public void comment(char[] ch, int start, int length) throws SAXException {
		checkInDocFlow();
		if(! stripComments) {
			preInsertContent(false);
			w.write("<!--");
			w.write(ch, start, length);
			w.write("-->");
		}
	}

	public void startCDATA() throws SAXException {
		checkInDocFlow();
		preInsertContent(false);
		w.write("<![CDATA[");
		withinCdata = true;
	}
	public void endCDATA() throws SAXException {
		checkInDocFlow();
		preInsertContent(false);
		withinCdata = false;
		w.write("]]>");
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		checkInDocFlow();
		preInsertContent(false);
		if(withinCdata) {
			w.write(ch, start, length);
		} else if(xmlEscapeAposAndQuote) {
			w.write(XML.xmlEscape(new String(ch, start, length)));
		} else {
			w.write(XML.xmlEscapeNoAposAndQuote(new String(ch, start, length)));
		}
	}


	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		checkInDocFlow();
		preInsertContent(false);
		w.write("<"+tagName(uri, localName, qName)+atts(atts)+consumePendingPrefixMappings());
		startTagJustSeen = true;
	}

	private String tagName(String uri, String localName, String qName) {
		if(qName != null) {
			return qName;
		} else if(STR.nullOrEmpty(uri)) {
			return localName;
		} else {
			for(Entry<String, String> entry : prefixMappings.entrySet()) {
				if(uri.equals(entry.getValue())) {
					return entry.getKey()+":"+localName;
				}
			}
			Check.illegalstate.fail();
		}
		return Check.ifNullOrEmpty(qName, localName);
	}
	private String consumePendingPrefixMappings() {
		if(pendingPrefixMappings.isEmpty()) {
			return "";
		} else {
			StringBuilder sb  = new StringBuilder();
			for(Entry<String, String> prefAndUri : pendingPrefixMappings.entrySet()) {
				sb.append(" ");
				String prefix = prefAndUri.getKey();
				String uri = prefAndUri.getValue();
				if("".equals(prefix)) {
					sb.append("xmlns=\"");
				} else {
					sb.append("xmlns:"+XML.xmlEscape(prefix)+"=\"");
				}
				sb.append(XML.xmlEscape(uri));
				sb.append("\"");
			}
			pendingPrefixMappings.clear();
			return sb.toString();
		}
	}

	private String atts(Attributes atts) {
		int len = atts.getLength();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < len; i++) {
			sb.append(" ");
			sb.append(atts.getQName(i));
			sb.append("=\"");
			sb.append(XML.xmlEscape(atts.getValue(i)));
			sb.append("\"");
		}
		return sb.toString();
	}
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		boolean emptyTag = startTagJustSeen;
		preInsertContent(true);
		checkInDocFlow();
		if(emptyTag && allowSingletonTag(uri, localName, qName)) {
			w.write("/>");
		} else {
			w.write("</"+tagName(uri, localName, qName)+">");
		}
	}

	private boolean allowSingletonTag(String uri, String localName, String qName) {
		return true;
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		checkInDocFlow();
		preInsertContent(true);
		if(! oneline) {
			w.write(ch, start, length);
		}
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		checkInDocFlow();
		preInsertContent(true);
	}

	//////////////////////////////////////////
	private void preInsertContent(boolean isEndTag) throws SAXException {
		if(startTagJustSeen && ! isEndTag) {
			w.write(">");
		}
		startTagJustSeen = false;
	}
	private void checkInDocFlow() throws SAXException {
		if(docStarted) {
			assertFalse(docEnded);
		} else {
			assertTrue(forrestMode);
		}
	}
	private void assertTrue(boolean cond) throws SAXException {
		if(! cond) {
			throw new SAXException();
		}
	}
	private void assertFalse(boolean cond) throws SAXException {
		if(cond) {
			throw new SAXException();
		}
	}
	//////////////////////////////////////////
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		throw new SAXException("unimplemented");
	}


	public void startEntity(String name) throws SAXException {
		throw new SAXException("unimplemented");
	}

	public void endDTD() throws SAXException {
		throw new SAXException("unimplemented");
	}


	public void endEntity(String name) throws SAXException {
		throw new SAXException("unimplemented");
	}

	public void skippedEntity(String name) throws SAXException {
		throw new SAXException("unimplemented");
	}

	public void setDocumentLocator(Locator locator) {
		//TODO Check.unsupportedoperation.fail();
	}

	private class SAXThrowingWriter {
		private Writer w;

		public SAXThrowingWriter(Writer writer) {
			w = writer;
		}

		public void write(char[] cbuf, int off, int len) throws SAXException {
			try {
				w.write(cbuf, off, len);
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}

		public void write(String str) throws SAXException {
			try {
				w.write(str);
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}

	}

	public void setWriter(Writer writer) {
		w = new SAXThrowingWriter(writer);
	}
}
