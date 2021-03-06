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
import java.util.function.Supplier;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.xml.ForwardingTransformerHandlerBase;
import com.nominanuda.zen.xml.SaxBuffer.SaxBit;
import com.nominanuda.zen.xml.SwallowingTransformerHandlerBase;

public abstract class DomManipulationHandler extends ForwardingTransformerHandlerBase {
	private int nestingLevel = 0;
	private final DomManipulationStmt stmt;
	private final JquerySelectorExpr jqSelector;
	private final Stack<Tuple2<Integer, Supplier<Void>>> triggerStack = new Stack<Tuple2<Integer,Supplier<Void>>>();
	protected final Stack<HtmlTag> parentElementStack = new Stack<HtmlTag>();
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
		jqSelector = new JquerySelectorExpr(stmt.getSelector());

	}

//	protected String getSelector() {
//		return stmt.getSelector();
//	}
//
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		nestingLevel++;
		if(matches(localName, atts, parentElementStack)) {
			onMatchedStartElement(uri, localName, qName, atts);
		} else {
			super.startElement(uri, localName, qName, atts);
		}
		parentElementStack.push(new HtmlTag(localName, atts));
	}

	protected abstract void onMatchedStartElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException;

	private void onTriggerEndElement() {
		Supplier<Void> f = triggerStack.pop().get1();
		f.get();
	}

	protected void pushTriggerEndElement(Supplier<Void> trigger) {
		triggerStack.push(new Tuple2<Integer, Supplier<Void>>(nestingLevel, trigger));
	};

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(! triggerStack.isEmpty() && triggerStack.peek().get0() == nestingLevel) {
			onTriggerEndElement();
		} else {
			super.endElement(uri, localName, qName);
		}
		parentElementStack.pop();
		nestingLevel--;
	}

	private boolean matches(String tag, Attributes atts, Stack<HtmlTag> parents) {
		return jqSelector.matches(tag, atts, parents);
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
			pushTriggerEndElement(new Supplier<Void>() {
				public Void get() {
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
			pushTriggerEndElement(new Supplier<Void>() {
				public Void get() {
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
			pushTriggerEndElement(new Supplier<Void>() {
				public Void get() {
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
			pushTriggerEndElement(new Supplier<Void>() {
				public Void get() {
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
