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
package com.nominanuda.dataobject;

import static com.nominanuda.dataobject.JsonSaxAdapter.*;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.code.Nullable;
import com.nominanuda.xml.SwallowingTransformerHandlerBase;

import static com.nominanuda.lang.Check.*;

public class SimpleJsonXmlTransformer extends SwallowingTransformerHandlerBase {
	private final String typeAttr;
	private final String uri;
	private final String prefix;
	private final String rootTag;

	private Stack<String> openKeys = new Stack<String>();
	private boolean isNextEventAKey = false;
	private DataType type = null;

	public SimpleJsonXmlTransformer(String rootTag) {
		this(null, null, rootTag);
	}

	public SimpleJsonXmlTransformer(@Nullable String uri, @Nullable String prefix, String rootTag) {
		this(uri, prefix, rootTag, null);
	}

	public SimpleJsonXmlTransformer(@Nullable String uri, @Nullable String prefix, String rootTag, @Nullable String typeAttr) {
		this.uri = uri;
		this.prefix = prefix;
		this.rootTag = notNull(rootTag);
		this.typeAttr = ifNullOrBlank(typeAttr, null);
	}

	public void startDocument() throws SAXException {
		getTarget().startDocument();
		if(uri != null) {
			getTarget().startPrefixMapping(prefix, uri);
		}
		openKeys.push(rootTag);
	}

	public void endDocument() throws SAXException {
		if(uri != null) {
			getTarget().endPrefixMapping(prefix);
		}
		getTarget().endDocument();
		illegalstate.assertEquals(rootTag, openKeys.pop());
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if(OBJECT_ENTRY_KEY_TAG.equals(localName)) {
			isNextEventAKey = true;
		} else if(DataType.number.name().equals(localName)) {
			type = DataType.number;
		} else if(DataType.string.name().equals(localName)) {
			type = DataType.string;
		} else if(DataType.bool.name().equals(localName)) {
			type = DataType.bool;
		} else if(DataType.nil.name().equals(localName)) {
			type = DataType.nil;
		} else if(DataType.object.name().equals(localName)) {
			getTarget().startElement(this.uri, openKeys.peek(), localNameToQName(openKeys.peek()), new AttributesImpl());
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(OBJECT_ENTRY_TAG.equals(localName)) {
			openKeys.pop();
		} else if(OBJECT_ENTRY_KEY_TAG.equals(localName)) {
			isNextEventAKey = false;
		} else if(DataType.object.name().equals(localName)) {
			getTarget().endElement(this.uri, openKeys.peek(), null);
		} 
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String s = new String(ch, start, length);
		if(isNextEventAKey) {
			openKeys.push(s);
		} else {
			getTarget().startElement(uri, openKeys.peek(), localNameToQName(openKeys.peek()), makeTypeAttribute(type));
			getTarget().characters(ch, start, length);
			getTarget().endElement(uri, openKeys.peek(), localNameToQName(openKeys.peek()));
		}
	}

	private @Nullable String localNameToQName(String localName) {
		return prefix == null ? localName : prefix + ":" + localName;
	}

	private Attributes makeTypeAttribute(DataType t) {
		if(typeAttr != null) {
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", typeAttr, typeAttr, "NMTOKEN", t.name());
			return atts;
		} else {
			return new AttributesImpl();
		}
	}

}
