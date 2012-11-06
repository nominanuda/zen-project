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

import java.util.Stack;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Fun0;
import com.nominanuda.lang.Strings;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.xml.ForwardingTransformerHandlerBase;
import com.nominanuda.xml.SwallowingTransformerHandlerBase;
import com.nominanuda.xml.SaxBuffer.SaxBit;

public abstract class DomManipulationHandler extends ForwardingTransformerHandlerBase {
	private int nestingLevel = 0;
	private final DomManipulationStmt stmt;
	private Stack<Tuple2<Integer, Fun0<Void>>> triggerStack = new Stack<Tuple2<Integer,Fun0<Void>>>();
	private ContentHandler liveContentHandler;
	private final static ContentHandler devNull = new SwallowingTransformerHandlerBase();

	public static DomManipulationHandler build(DomManipulationStmt stmt) {
		switch (stmt.getOperation()) {
		case html:
			return new HtmlHandler(stmt);
		case replaceWith:
			return new ReplaceWithHandler(stmt);
		case before:
			return new BeforeHandler(stmt);
		case after:
			return new AfterHandler(stmt);
		case prepend:
			return new PrependHandler(stmt);
		case append:
			return new AppendHandler(stmt);
		}
		throw new IllegalStateException("unsupported operation:" + stmt.getOperation().name());
	}

	private DomManipulationHandler(DomManipulationStmt domManipulationStmt) {
		stmt = domManipulationStmt;
	}

	protected String getSelector() {
		return stmt.getSelector();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		nestingLevel++;
		if(matches(localName, atts)) {
			onMatchedStartElement(uri, localName, qName, atts);
		} else {
			super.startElement(uri, localName, qName, atts);
		}
	}

	protected abstract void onMatchedStartElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException;

	private void onTriggerEndElement() {
		Fun0<Void> f = triggerStack.pop().get1();
		f.apply();
	}

	protected void pushTriggerEndElement(Fun0<Void> trigger) {
		triggerStack.push(new Tuple2<Integer, Fun0<Void>>(nestingLevel, trigger));
	};

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(! triggerStack.isEmpty() && triggerStack.peek().get0() == nestingLevel) {
			onTriggerEndElement();
		} else {
			super.endElement(uri, localName, qName);
		}
		nestingLevel--;
	}

	//TODO
	private boolean matches(String tag, Attributes atts) {
		if(getSelector().startsWith(".")) {
			if(classMatches(atts.getValue("class"), getSelector().substring(1))) {
				return true;
			}
		} else if(true) {
			if(tag.equals(getSelector())) {
				return true;
			}
		}
		return false;//TODO
	}

	private boolean classMatches(@Nullable String clsAttr, String targetClass) {
		return clsAttr != null &&  Strings.splitAndTrim(clsAttr, "\\s+").contains(targetClass);
	}

	protected void turnOnOutput() {
		if(liveContentHandler == null) {
			liveContentHandler = getTarget();
		}
		setResult(new SAXResult(liveContentHandler));
	}
	protected void turnOffOutput() {
		if(liveContentHandler == null) {
			liveContentHandler = getTarget();
		}
		setResult(new SAXResult(devNull));
	}
	protected void streamFragment() throws SAXException {
		ContentHandler ch = getTarget();
		for(SaxBit b : stmt.getSaxBuffer().getBits()) {
			b.send(ch);
		}
	}

	private static class HtmlHandler extends DomManipulationHandler {
		public HtmlHandler(DomManipulationStmt domManipulationStmt) {
			super(domManipulationStmt);
		}
		@Override
		protected void onMatchedStartElement(final String uri, final String localName,
				final String qName, final Attributes atts) throws SAXException {
			turnOffOutput();
			pushTriggerEndElement(new Fun0<Void>() {
				public Void apply() {
					try {
						turnOnOutput();
						getTarget().startElement(uri, localName, qName, atts);
						streamFragment();
						getTarget().endElement(uri, localName, qName);
						return null;
					} catch (SAXException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	private static class ReplaceWithHandler extends DomManipulationHandler {
		public ReplaceWithHandler(DomManipulationStmt domManipulationStmt) {
			super(domManipulationStmt);
		}
		@Override
		protected void onMatchedStartElement(final String uri, final String localName,
				final String qName, final Attributes atts) throws SAXException {
			turnOffOutput();
			pushTriggerEndElement(new Fun0<Void>() {
				public Void apply() {
					try {
						turnOnOutput();
						streamFragment();
						return null;
					} catch (SAXException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	private static class BeforeHandler extends DomManipulationHandler {
		public BeforeHandler(DomManipulationStmt domManipulationStmt) {
			super(domManipulationStmt);
		}
		@Override
		protected void onMatchedStartElement(final String uri, final String localName,
				final String qName, final Attributes atts) throws SAXException {
			streamFragment();
			getTarget().startElement(uri, localName, qName, atts);
		}
	}
	private static class PrependHandler extends DomManipulationHandler {
		public PrependHandler(DomManipulationStmt domManipulationStmt) {
			super(domManipulationStmt);
		}
		@Override
		protected void onMatchedStartElement(final String uri, final String localName,
				final String qName, final Attributes atts) throws SAXException {
			getTarget().startElement(uri, localName, qName, atts);
			streamFragment();
		}
	}
	private static class AppendHandler extends DomManipulationHandler {
		public AppendHandler(DomManipulationStmt domManipulationStmt) {
			super(domManipulationStmt);
		}
		@Override
		protected void onMatchedStartElement(final String uri, final String localName,
				final String qName, final Attributes atts) throws SAXException {
			getTarget().startElement(uri, localName, qName, atts);
			pushTriggerEndElement(new Fun0<Void>() {
				public Void apply() {
					try {
						streamFragment();
						getTarget().endElement(uri, localName, qName);
						return null;
					} catch (SAXException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	private static class AfterHandler extends DomManipulationHandler {
		public AfterHandler(DomManipulationStmt domManipulationStmt) {
			super(domManipulationStmt);
		}
		@Override
		protected void onMatchedStartElement(final String uri, final String localName,
				final String qName, final Attributes atts) throws SAXException {
			getTarget().startElement(uri, localName, qName, atts);
			pushTriggerEndElement(new Fun0<Void>() {
				public Void apply() {
					try {
						getTarget().endElement(uri, localName, qName);
						streamFragment();
						return null;
					} catch (SAXException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}

}
