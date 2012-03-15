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
package com.nominanuda.web.mvc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.nominanuda.lang.Strings;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.lang.Tuple3;
import com.nominanuda.saxpipe.ForwardingTransformerHandlerBase;
import com.nominanuda.saxpipe.SaxBuffer;
import com.nominanuda.saxpipe.SaxBuffer.SaxBit;

public class HtmlComposer {
	public enum DomOp {
		prepend,append,before,after
	}
	//private final ContentHandler target;
	private final Handler h;
//	private void check() {
//		Check.illegalstate.assertTrue(running, "HtmlComposer cannot be used as a Content");
//	}
	public HtmlComposer(/*DataObject conf, */ContentHandler target) {
		//this.target = target;
		h = new Handler();
		h.setResult(new SAXResult(target));
	}

	List<Tuple3<String, SaxBuffer, DomOp>> fragments;
	Stack<Tuple2<Integer, Iterator<SaxBit>>> playing = new Stack<Tuple2<Integer,Iterator<SaxBit>>>();

	public void render(List<Tuple3<String, SaxBuffer, DomOp>> viewMap) throws SAXException {
		fragments = viewMap;
		playing.push(new Tuple2<Integer, Iterator<SaxBit>>(0, viewMap.get(0).get1().getBits().iterator()));
		while(true) {
			Iterator<SaxBit> itr = playing.peek().get1();
			if(itr.hasNext()) {
				SaxBit bit = itr.next();
				h.sendSaxBit(bit);
			} else {
				playing.pop();
				if(playing.isEmpty()) {
					break;
				} else {
					continue;
				}
			}
		}
	}

	private class DocContext implements ContentHandler {
		private Stack<String> elementsStack = new Stack<String>();
		private Stack<List<String>> classesStack = new Stack<List<String>>();

		public boolean matches(String selector) {//TODO
			return !classesStack.isEmpty()
				&& selector.startsWith(".") 
				&& classesStack.peek().contains(selector.substring(1));
		}
		List<String> THE_EMPTY_LIST = Collections.emptyList();
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			if(! ("html".equals(localName)||"body".equals(localName))) {
				elementsStack.push(localName);
				String classAttVal = atts.getValue("class");
				List<String> classes = Strings.nullOrBlank(classAttVal)
					? THE_EMPTY_LIST
					: Strings.splitAndTrim(classAttVal, "\\s+");
				classesStack.push(classes);
			}
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
	private void on(DocContext cx, DomOp op) {
		int insertionCtxOrder = playing.peek().get0();
		for(int i = insertionCtxOrder + 1; i < fragments.size(); i++) {
			String sel = fragments.get(i).get0();
			if(cx.matches(sel) && op == fragments.get(i).get2()) {
				playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, fragments.get(i).get1().getBits().iterator()));
			}
		}
	}
	Writer ww = new OutputStreamWriter(System.err);
	private class Handler extends ForwardingTransformerHandlerBase {
		private DocContext docContext = new DocContext();
		private int numDocStarted = 0;

		public void sendSaxBit(SaxBit bit) throws SAXException {
			bit.send(docContext);
			on(docContext, DomOp.before);
try {
	bit.dump(ww);
	ww.flush();
} catch (IOException e) {
	throw new RuntimeException(e);
}
			bit.send(this);
			on(docContext, DomOp.after);
		}

		public void startDocument() throws SAXException {
			if(numDocStarted++ < 1) {
				super.startDocument();
			}
		}

		public void endDocument() throws SAXException {
			if(--numDocStarted < 1) {
				super.endDocument();
			}
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			super.startElement(uri, localName, qName, atts);
			on(docContext, DomOp.prepend);
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			on(docContext, DomOp.append);
			super.endElement(uri, localName, qName);
		}
	}
}
