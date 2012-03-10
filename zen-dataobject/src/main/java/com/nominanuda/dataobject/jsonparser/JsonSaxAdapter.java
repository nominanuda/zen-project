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
package com.nominanuda.dataobject.jsonparser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.DataType;
import com.nominanuda.lang.Check;

import static com.nominanuda.dataobject.DataType.*;

public class JsonSaxAdapter implements JsonContentHandler {
	public static final String JSON_SAX = "urn:JSON_SAX_NS";
	public static final String JSON_SAX_NS = "urn:JSON_SAX_NS";

	private static final DataStructHelper dataStructHelper = new DataStructHelper();
	public static final String OBJECT_TAG = object.name();
	public static final String OBJECT_ENTRY_TAG = "objectEntry";
	public static final String OBJECT_ENTRY_KEY_TAG = "key";
	public static final String OBJECT_ENTRY_VALUE_TAG = "value";
	public static final String ARRAY_TAG = array.name();

	private final Attributes EMPTY_ATTRS = new AttributesImpl();
	private final ContentHandler ch;

	public JsonSaxAdapter(ContentHandler ch) {
		this.ch = ch;
	}

	@Override
	public void startJSON() throws SAXException {
		ch.startDocument();
		ch.startPrefixMapping(JSON_SAX, JSON_SAX_NS);
	}

	@Override
	public void endJSON() throws SAXException {
		ch.endPrefixMapping(JSON_SAX);
		ch.endDocument();
	}

	private String qname(String localName) {
		return JSON_SAX_NS+":"+localName;
	}

	@Override
	public boolean startObject() throws SAXException {
		ch.startElement(JSON_SAX_NS, OBJECT_TAG, qname(OBJECT_TAG), EMPTY_ATTRS);
		return true;
	}

	@Override
	public boolean endObject() throws SAXException {
		ch.endElement(JSON_SAX_NS, OBJECT_TAG, qname(OBJECT_TAG));
		return true;
	}

	@Override
	public boolean startObjectEntry(String key) throws SAXException {
		ch.startElement(JSON_SAX_NS, OBJECT_ENTRY_TAG, qname(OBJECT_ENTRY_TAG), EMPTY_ATTRS);
		ch.startElement(JSON_SAX_NS, OBJECT_ENTRY_KEY_TAG, qname(OBJECT_ENTRY_KEY_TAG), EMPTY_ATTRS);
		ch.characters(key.toCharArray(), 0, key.length());
		ch.endElement(JSON_SAX_NS, OBJECT_ENTRY_KEY_TAG, qname(OBJECT_ENTRY_KEY_TAG));
		ch.startElement(JSON_SAX_NS, OBJECT_ENTRY_VALUE_TAG, qname(OBJECT_ENTRY_VALUE_TAG), EMPTY_ATTRS);
		return true;
	}

	@Override
	public boolean endObjectEntry() throws SAXException {
		ch.endElement(JSON_SAX_NS, OBJECT_ENTRY_VALUE_TAG, qname(OBJECT_ENTRY_VALUE_TAG));
		ch.endElement(JSON_SAX_NS, OBJECT_ENTRY_TAG, qname(OBJECT_ENTRY_TAG));
		return true;
	}

	@Override
	public boolean startArray() throws SAXException {
		ch.startElement(JSON_SAX_NS, ARRAY_TAG, qname(ARRAY_TAG), EMPTY_ATTRS);
		return true;
	}

	@Override
	public boolean endArray() throws SAXException {
		ch.endElement(JSON_SAX_NS, ARRAY_TAG, qname(ARRAY_TAG));
		return true;
	}

	@Override
	public boolean primitive(Object value) throws SAXException {
		Check.illegalstate.assertTrue(dataStructHelper.isPrimitiveOrNull(value));
		DataType t = dataStructHelper.getDataType(value);
		switch (t) {
		case nil:
			ch.startElement(JSON_SAX_NS, nil.name(), qname(nil.name()), EMPTY_ATTRS);
			ch.endElement(JSON_SAX_NS, nil.name(), qname(nil.name()));
			break;
		default:
			String v = dataStructHelper.primitiveOrNullToString(value);
			ch.startElement(JSON_SAX_NS, t.name(), qname(t.name()), EMPTY_ATTRS);
			ch.characters(v.toCharArray(), 0, v.length());
			ch.endElement(JSON_SAX_NS, t.name(), qname(t.name()));
			break;
		}
		return true;
	}
}
