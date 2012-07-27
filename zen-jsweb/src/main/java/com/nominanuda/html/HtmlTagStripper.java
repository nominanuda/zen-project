package com.nominanuda.html;

import java.util.concurrent.atomic.AtomicInteger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.nominanuda.code.ThreadSafe;
import com.nominanuda.xml.ForwardingTransformerHandlerBase;

@ThreadSafe
public class HtmlTagStripper extends ForwardingTransformerHandlerBase {
	private final String[] tagsToStrip;
	private final ThreadLocal<AtomicInteger> nestingCounter = new ThreadLocal<AtomicInteger>() {
		protected AtomicInteger initialValue() {
			return new AtomicInteger(0);
		}
	};
	public HtmlTagStripper(String[] tagsToStrip) {
		this.tagsToStrip = tagsToStrip;
	}
	@Override
	public void startDocument() throws SAXException {
		nestingCounter.get().set(0);
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if(nestingCounter.get().get() < 1) {
			if(isForbidden(uri, localName, qName, atts)) {
				nestingCounter.get().incrementAndGet();
			} else {
				super.startElement(uri, localName, qName, atts);
			}
		} else {
			nestingCounter.get().incrementAndGet();
		}
	}

	@Override 
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(nestingCounter.get().get() < 1) {
			super.endElement(uri, localName, qName);
		} else {
			nestingCounter.get().decrementAndGet();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(nestingCounter.get().get() < 1) {
			super.characters(ch, start, length);
		}
	}
	private boolean isForbidden(String uri, String localName, String qName, Attributes atts) {
		for(String s : tagsToStrip) {
			if(localName.equals(s)) {
				return true;
			}
		}
		return false;
	}
}
