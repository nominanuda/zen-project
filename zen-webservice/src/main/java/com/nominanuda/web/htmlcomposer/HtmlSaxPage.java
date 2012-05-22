package com.nominanuda.web.htmlcomposer;

import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.saxpipe.SAXEmitter;
import com.nominanuda.saxpipe.SaxBuffer;
import com.nominanuda.saxpipe.SaxBuffer.SaxBit;

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
