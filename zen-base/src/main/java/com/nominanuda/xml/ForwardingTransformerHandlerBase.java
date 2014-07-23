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

import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import com.nominanuda.lang.Check;


public class ForwardingTransformerHandlerBase implements TransformerHandler {
	protected static final XmlHelper saxHelper = new XmlHelper();
	@SuppressWarnings("unused")
	private Locator locator;//TODO
	private ContentHandler contentHandler;
	private LexicalHandler lexicalHandler = new NullLexicalHandler();
	private DTDHandler dtdHandler = new NullDTDHandler();
	private URI baseURI;

	protected ContentHandler getTarget() {
		return contentHandler;
	}

	protected LexicalHandler getLexicalTarget() {
		return lexicalHandler;
	}

	protected DTDHandler getDTDTarget() {
		return dtdHandler;
	}

	//TransformerHandler
	public void setResult(Result result) throws IllegalArgumentException {
		Check.notNull(result);
		if(result instanceof SAXResult) {
			setTarget((SAXResult)result);
		} else {
			TransformerHandler th = saxHelper.newIdentityTransformerHandler();
			th.setResult(result);
			setTarget(new SAXResult(th));
		}
	}

	private void setTarget(SAXResult result) {
		ContentHandler ch = result.getHandler();
		Check.notNull(ch);
		contentHandler = ch;
		LexicalHandler lh = result.getLexicalHandler();
		if(lh != null) {
			lexicalHandler = lh;
			if(lexicalHandler instanceof DTDHandler) {
				dtdHandler = (DTDHandler)lexicalHandler;
			}
		}
		if(contentHandler instanceof DTDHandler) {
			dtdHandler = (DTDHandler)contentHandler;
		}
	}

	public void setSystemId(String systemID) {
		Check.notNull(systemID);
		baseURI = URI.create(systemID);
		Check.illegalargument.assertTrue(
			baseURI.isAbsolute() || systemID.startsWith("/"));
	}

	public String getSystemId() {
		return baseURI.toString();
	}

	public Transformer getTransformer() {
		return new TransformerObject();
	}

	//ContentHandler
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public void startDocument() throws SAXException {
		getTarget().startDocument();
	}

	public void endDocument() throws SAXException {
		getTarget().endDocument();
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		getTarget().startPrefixMapping(prefix, uri);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		getTarget().endPrefixMapping(prefix);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		getTarget().startElement(uri, localName, qName, atts);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		getTarget().endElement(uri, localName, qName);
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		getTarget().characters(ch, start, length);
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		getTarget().ignorableWhitespace(ch, start, length);
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		getTarget().processingInstruction(target, data);
	}

	public void skippedEntity(String name) throws SAXException {
		getTarget().skippedEntity(name);
	}

	//LexicalHandler
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		getLexicalTarget().startDTD(name, publicId, systemId);
	}

	public void endDTD() throws SAXException {
		getLexicalTarget().endDTD();
	}

	public void startEntity(String name) throws SAXException {
		getLexicalTarget().startEntity(name);
	}

	public void endEntity(String name) throws SAXException {
		getLexicalTarget().endEntity(name);
	}

	public void startCDATA() throws SAXException {
		getLexicalTarget().startCDATA();
	}

	public void endCDATA() throws SAXException {
		getLexicalTarget().endCDATA();
	}

	public void comment(char[] ch, int start, int length) throws SAXException {
		getLexicalTarget().comment(ch, start, length);
	}

	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		getDTDTarget().notationDecl(name, publicId, systemId);
	}

	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		getDTDTarget().unparsedEntityDecl(name, publicId, systemId, notationName);
	}
}
