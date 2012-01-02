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
package com.nominanuda.io.saxpipe;

import static com.nominanuda.lang.Collections.EMPTY_STR_ARR;
import static com.nominanuda.lang.Collections.array;
import static com.nominanuda.lang.Collections.buildMap;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.lang.Strings;

public class HtmlPurifyTransformer extends ForwardingTransformerHandlerBase {

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		Attributes atts2 = filterAttributes(localName.toLowerCase(), atts);
		super.startElement(uri, localName, qName, atts2);
	}

	@Override 
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
	}

	private Attributes filterAttributes(String tag, Attributes atts) {
		AttributesImpl atts2 = new AttributesImpl();
		String[] allowed = allowedAttributesByTag(tag);
		for(String name : allowed) {
			String v = atts.getValue(name);
			if(Strings.notNullOrBlank(v)) {
				atts2.addAttribute("", name, "", "nmtokens", v);
			}
		}
		for(String name : allowedAttributesOnAllTags) {
			String v = atts.getValue(name);
			if(Strings.notNullOrBlank(v)) {
				atts2.addAttribute("", name, "", "nmtokens", v);
			}
		}
		return atts2;
	}
	private String[] allowedAttributesByTag(String tag) {
		String[] s = attsMap.get(tag);
		return s == null ? EMPTY_STR_ARR : s;
	}
	private String[] allowedAttributesOnAllTags = array("title", "alt");

	private Map<String, String[]> attsMap = buildMap(HashMap.class, 
		"img", array("src"),
		"a", array("href")
	);
}
