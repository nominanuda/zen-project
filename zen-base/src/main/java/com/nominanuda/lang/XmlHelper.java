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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
	private final SAXTransformerFactory txFactory = 
					(SAXTransformerFactory) SAXTransformerFactory.newInstance();
	{
		try {
			identity = txFactory.newTemplates(new StreamSource(
					new StringReader(identityXslt)));
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
		SAXParser sp = newParser();
		doc = newDocument();
		DOMBuilder domBuilder = new DOMBuilder(doc);
		XMLReader xr = sp.getXMLReader();

		XMLFilterImpl xfi = new WhiteSpaceIgnoringFilter(xr);
		xfi.setContentHandler(domBuilder);
		xfi.parse(is);
		return doc;
	}

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
			// return identity.newTransformer();
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

	public static final String identityXslt = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
			+ "<xsl:template match=\"/|@*|node()\">"
			+ "<xsl:copy>"
			+ "<xsl:apply-templates select=\"@* | node()\"/>"
			+ "</xsl:copy>"
			+ "</xsl:template>" + "</xsl:stylesheet>";

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
	
	public String serializeUtf8(Node node) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serialize(node, baos);
		return new String(baos.toByteArray(), Strings.UTF8);
	}

	public void serialize(Node node, OutputStream out) {
		Properties outputProperties = getDefaultSerilizationProperties();
		DocumentType docType = null;
		if (node instanceof Document) {
			docType = ((Document) node).getDoctype();
		} else {
			docType = node.getOwnerDocument().getDoctype();
		}
		if (docType != null) {
			// TODO: set method. Actually it break the format end remove xml
			// declaration
			// outputProperties.put(OutputKeys.METHOD, docType.getName());
			outputProperties.put(OutputKeys.DOCTYPE_PUBLIC,
					docType.getPublicId());
			outputProperties.put(OutputKeys.DOCTYPE_SYSTEM,
					docType.getSystemId());
		}
		serialize(node, out, outputProperties);
	}

	public void serialize(Node node, OutputStream out,
			Properties outputProperties) {
		try {
			Source xmlSource = new DOMSource(node);
			Result output = new StreamResult(out);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperties(outputProperties);
			transformer.transform(xmlSource, output);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Properties getDefaultSerilizationProperties() {
		Properties outputProperties = new Properties();
		outputProperties.put(OutputKeys.INDENT, "yes");
		outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
		return outputProperties;
	}

	public String xPathForString(Object nodeOrNodeList, String xpathExpr,
			String... nsBindings) throws IllegalArgumentException {
		return (String) xPath(nodeOrNodeList, xpathExpr, XPathConstants.STRING, nsBindings);
	}

	public Number xPathForNumber(Object nodeOrNodeList, String xpathExpr,
			String... nsBindings) throws IllegalArgumentException {
		return (Number) xPath(nodeOrNodeList, xpathExpr, XPathConstants.NUMBER, nsBindings);
	}

	public Boolean xPathForBoolean(Object nodeOrNodeList, String xpathExpr,
			String... nsBindings) throws IllegalArgumentException {
		return (Boolean) xPath(nodeOrNodeList, xpathExpr, XPathConstants.BOOLEAN,
				nsBindings);
	}

	public NodeList xPathForNodeList(Object nodeOrNodeList, String xpathExpr,
			String... nsBindings) throws IllegalArgumentException {
		return (NodeList) xPath(nodeOrNodeList, xpathExpr, XPathConstants.NODESET,
				nsBindings);
	}

	public Node xPathForNode(Object nodeOrNodeList, String xpathExpr,
			String... nsBindings) throws IllegalArgumentException {
		return (Node) xPath(nodeOrNodeList, xpathExpr, XPathConstants.NODE, nsBindings);
	}

	/**
	 * 
	 * @param nodeOrNodeList
	 * @param xpathExpr
	 * @param nsBindings in the form "n1","http://a.b.c/n1", "prefix2", "http://a.b.c/whatever"
	 * @return
	 * @throws XPathExpressionException 
	 */
	private Object xPath(Object nodeOrNodeList, String xpathExpr, QName resultType, String... nsBindings)
			throws IllegalArgumentException {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new MyNamespaceContext(nsBindings));
			XPathExpression expr = xpath.compile(xpathExpr);
			Object result = expr.evaluate(nodeOrNodeList, resultType);
			return result;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static class MyNamespaceContext implements NamespaceContext {
		private String[] nsBindings = new String[0];

		public MyNamespaceContext(String[] nsBindings) {
			if(nsBindings != null) {
				this.nsBindings= nsBindings;
			}
			Check.illegalargument.assertTrue(this.nsBindings.length % 2 == 0);
		}

		public String getNamespaceURI(String prefix) {
			for(int i = 0; i < nsBindings.length; i += 2) {
				if(nsBindings[i].equals(prefix)) {
					return nsBindings[i+1];
				}
			}
			return XMLConstants.NULL_NS_URI;
		}

		public String getPrefix(String namespace) {
			if(nsBindings.length == 0) {
				return null;
			} else {
				for(int i = 1; i < nsBindings.length; i += 2) {
					if(nsBindings[i].equals(namespace)) {
						return nsBindings[i-1];
					}
				}
				return null;
			}
		}

		public Iterator<?> getPrefixes(String namespace) {
			return Arrays.asList(getPrefix(namespace)).iterator();
		}
	}
}
