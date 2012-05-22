package com.nominanuda.web.htmlcomposer;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.nominanuda.lang.Strings;

public class DocContext implements ContentHandler {
	private Stack<String> elementsStack = new Stack<String>();
	private Stack<List<String>> classesStack = new Stack<List<String>>();
	private Logger log = LoggerFactory.getLogger(getClass());

	public boolean matches(String selector) {//TODO
		return !classesStack.isEmpty()
			&& selector.startsWith(".") 
			&& classesStack.peek().contains(selector.substring(1));
	}
	List<String> THE_EMPTY_LIST = Collections.emptyList();
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		//log.debug("startElement {}",localName);
		if(! ("html".equals(localName)||"body".equals(localName))) {
			elementsStack.push(localName);
			String classAttVal = atts.getValue("class");
			List<String> classes = Strings.nullOrBlank(classAttVal)
				? THE_EMPTY_LIST
				: Strings.splitAndTrim(classAttVal, "\\s+");
			classesStack.push(classes);
		}
		//log.debug("stack:",elementsStack);
	}
	public void removeLast() {
		elementsStack.pop();
		classesStack.pop();
	}
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(! ("html".equals(localName)||"body".equals(localName))) {
			elementsStack.pop();
			classesStack.pop();
		}
	}
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}
	////////////////////////
	public void endPrefixMapping(String prefix) throws SAXException {
	}
	public void endDocument() throws SAXException {
	}
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}
	public void processingInstruction(String target, String data)
			throws SAXException {
	}
	public void setDocumentLocator(Locator locator) {
	}
	public void skippedEntity(String name) throws SAXException {
	}
	public void startDocument() throws SAXException {
	}
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}
}