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
 * 
 */
package com.nominanuda.zen.obj;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class JsonSaxAdapter implements JixHandler {
	public static final String JSON_SAX = "urn:JSON_SAX_NS";
	public static final String JSON_SAX_NS = "urn:JSON_SAX_NS";

	public static final String OBJECT_TAG = "obj";
	public static final String OBJECT_ENTRY_TAG = "entry";
	public static final String OBJECT_ENTRY_KEY_TAG = "key";
	public static final String OBJECT_ENTRY_VALUE_TAG = "val";
	public static final String ARRAY_TAG = "arr";
	public static final String NULL_VALUE = "null";

	private final Attributes EMPTY_ATTRS = new AttributesImpl();
	private final ContentHandler ch;

	private int nestingLevel = -1;
	private Stack<Integer> entryNestingLevels = new Stack<>();
	public JsonSaxAdapter(ContentHandler ch) {
		this.ch = ch;
	}

	private void beforeEnterValue() {
		nestingLevel++;
		if(nestingLevel == 0) {
			startJSON();
		}
	}

	private void beforeEnterKey() {
		entryNestingLevels.push(nestingLevel);
	}

	private void afterExitValue() {
		if(nestingLevel == 0) {
			endJSON();
		} else {
			if(nestingLevel == entryNestingLevels.peek()) {
				entryNestingLevels.pop();
				endObjectEntry();
			}
		}
		nestingLevel--;
	}

	private void startJSON() throws RuntimeException {
		try {
			ch.startDocument();
			ch.startPrefixMapping(JSON_SAX, JSON_SAX_NS);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	private void endJSON() throws RuntimeException {
		try {
			ch.endPrefixMapping(JSON_SAX);
			ch.endDocument();
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	private String qname(String localName) {
		return JSON_SAX_NS + ":" + localName;
	}

	@Override
	public void startObj() throws RuntimeException {
		beforeEnterValue();
		try {
			ch.startElement(JSON_SAX_NS, OBJECT_TAG, qname(OBJECT_TAG),
					EMPTY_ATTRS);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void endObj() throws RuntimeException {
		try {
			ch.endElement(JSON_SAX_NS, OBJECT_TAG, qname(OBJECT_TAG));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		afterExitValue();
	}

	@Override
	public void key(Key key) throws RuntimeException {
		beforeEnterKey();
		try {
			String _key = key.get();
			ch.startElement(JSON_SAX_NS, OBJECT_ENTRY_TAG,
					qname(OBJECT_ENTRY_TAG), EMPTY_ATTRS);
			ch.startElement(JSON_SAX_NS, OBJECT_ENTRY_KEY_TAG,
					qname(OBJECT_ENTRY_KEY_TAG), EMPTY_ATTRS);
			ch.characters(_key.toCharArray(), 0, _key.length());
			ch.endElement(JSON_SAX_NS, OBJECT_ENTRY_KEY_TAG,
					qname(OBJECT_ENTRY_KEY_TAG));
			ch.startElement(JSON_SAX_NS, OBJECT_ENTRY_VALUE_TAG,
					qname(OBJECT_ENTRY_VALUE_TAG), EMPTY_ATTRS);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	private void endObjectEntry() throws RuntimeException {
		try {
			ch.endElement(JSON_SAX_NS, OBJECT_ENTRY_VALUE_TAG,
					qname(OBJECT_ENTRY_VALUE_TAG));
			ch.endElement(JSON_SAX_NS, OBJECT_ENTRY_TAG,
					qname(OBJECT_ENTRY_TAG));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startArr() throws RuntimeException {
		beforeEnterValue();
		try {
			ch.startElement(JSON_SAX_NS, ARRAY_TAG, qname(ARRAY_TAG),
					EMPTY_ATTRS);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void endArr() throws RuntimeException {
		try {
			ch.endElement(JSON_SAX_NS, ARRAY_TAG, qname(ARRAY_TAG));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		afterExitValue();
	}

	@Override
	public void val(Val value) throws RuntimeException {
		try {
			beforeEnterValue();
			JsonType t = value.getType();
			if(value.isNull()) {
				ch.startElement(JSON_SAX_NS, NULL_VALUE, qname(NULL_VALUE),
						EMPTY_ATTRS);
				ch.endElement(JSON_SAX_NS, NULL_VALUE, qname(NULL_VALUE));
			} else {
				String v = value.toJavaObjModel().toString();
				ch.startElement(JSON_SAX_NS, t.name(), qname(t.name()),
						EMPTY_ATTRS);
				ch.characters(v.toCharArray(), 0, v.length());
				ch.endElement(JSON_SAX_NS, t.name(), qname(t.name()));
			}
			afterExitValue();
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}
}
