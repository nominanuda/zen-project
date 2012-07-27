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
