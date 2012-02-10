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
package com.nominanuda.lang;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.nominanuda.code.ThreadSafe;

@ThreadSafe
public class XmlHelper {

	private final ThreadLocal<DocumentBuilder> documentBuilder = new ThreadLocal<DocumentBuilder>() {
		@Override
		protected DocumentBuilder initialValue() {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			try {
				return dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new IllegalStateException(e);
			}
		}
	};
	private final SAXTransformerFactory txFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
	{
		try {
			identity = txFactory.newTemplates(new StreamSource(new StringReader(identityXslt)));
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
	private final Templates identity;


	public Document newDocument() {
		return documentBuilder.get().newDocument();
	}

	public String xmlEscape(String s) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			switch (character) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&apos;");
				break;
			case '&':
				result.append("&amp;");
				break;
			default:
				result.append(character);
				break;
			}
			character = iterator.next();
		}
		return result.toString();

	}
	public String xmlEscapeNoAposAndQuote(String s) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			switch (character) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			default:
				result.append(character);
				break;
			}
			character = iterator.next();
		}
		return result.toString();
	}
	public Document parseAsDocument(InputSource is) throws SAXException,
    IOException {
Document doc;
try {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser sp = spf.newSAXParser();
    doc = newDocument();
    DOMBuilder domBuilder = new DOMBuilder(doc);
    XMLReader xr = sp.getXMLReader();

    XMLFilterImpl xfi = new WhiteSpaceIgnoringFilter(xr);
    xfi.setContentHandler(domBuilder);
    xfi.parse(is);
    // doc = documentBuilderFactory.newDocumentBuilder().parse(is);
} catch (ParserConfigurationException e) {
    throw new SAXException(e);
}
return doc;

}
    // public Document newDocument(String ns) {
    // try {
    // DOMImplementation domImpl =
    // documentBuilderFactory.newDocumentBuilder().getDOMImplementation();
    // Document doc = domImpl.createDocument(ns, "foo", null);
    // return doc;
    // //return documentBuilderFactory.newDocumentBuilder().newDocument();
    // } catch (ParserConfigurationException e) {
    // throw new IllegalStateException(e);
    // }
    // }
    private class WhiteSpaceIgnoringFilter extends XMLFilterImpl {

            public WhiteSpaceIgnoringFilter(XMLReader xr) {
                    super(xr);
            }

            @Override
            public void characters(char[] ch, int start, int length)
                            throws SAXException {
                    if (isWhiteSpace(ch, start, length)) {
                            // super.ignorableWhitespace(ch, start, length);
                    } else {
                            super.characters(ch, start, length);
                    }
            }

    }
	public boolean isWhiteSpace(char ch[], int start, int length) {
		int last = start + length;
		for (int i = start; i < last; i++) {
			if (!Character.isWhitespace(ch[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean isWhiteSpace(String str) {
		return isWhiteSpace(str.toCharArray(), 0, str.length());
	}

	public TransformerHandler newIdentityTransformerHandler() {
		try {
			return txFactory.newTransformerHandler(identity);
			//return identity.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
	public TransformerHandler xslTransformerHandler(Reader xslt) {
		try {
			return txFactory.newTransformerHandler(xslTemplates(xslt));
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
	public TransformerHandler xslTransformerHandler(Templates xslt) {
		try {
			return txFactory.newTransformerHandler(xslt);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
	public Templates xslTemplates(Reader xslt) {
		try {
			return txFactory.newTemplates(new StreamSource(xslt));
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	public static final String identityXslt =
		"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" +
		"<xsl:template match=\"/|@*|node()\">" +
		"<xsl:copy>" +
		"<xsl:apply-templates select=\"@* | node()\"/>" +
		"</xsl:copy>" +
		"</xsl:template>" +
		"</xsl:stylesheet>";

	public SAXParser newParser() throws SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException(e);
		}
		return sp;
	}
}
