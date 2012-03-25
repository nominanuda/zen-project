package com.nominanuda.saxpipe;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * A class that can record SAX events and replay them later.
 * 
 * <p>
 * Use this class if you need to frequently generate smaller amounts of SAX
 * events, or replay a set of recorded start events immediately.
 * </p>
 * 
 * <p>
 * Both {@link ContentHandler} and {@link LexicalHandler} are supported, the
 * only exception is that the setDocumentLocator event is not recorded.
 * </p>
 * 
 * @version $Id: SaxBuffer.java 696943 2008-09-19 06:56:50Z reinhard $
 */
public class SaxBuffer implements ContentHandler, LexicalHandler, Serializable,
		SAXEmitter {
	private static final long serialVersionUID = 535933679567154884L;
	/**
	 * Stores list of {@link SaxBit} objects.
	 */
	protected List<SaxBit> saxbits;

	/**
	 * Creates empty SaxBuffer
	 */
	public SaxBuffer() {
		this.saxbits = new ArrayList<SaxBit>();
	}

	/**
	 * Creates SaxBuffer based on the provided bits list.
	 */
	public SaxBuffer(List<SaxBit> bits) {
		this.saxbits = bits;
	}

	/**
	 * Creates copy of another SaxBuffer
	 */
	public SaxBuffer(SaxBuffer saxBuffer) {
		this.saxbits = new ArrayList<SaxBit>(saxBuffer.saxbits);
	}

	//
	// ContentHandler Interface
	//

	public void skippedEntity(String name) throws SAXException {
		this.saxbits.add(new SkippedEntity(name));
	}

	public void setDocumentLocator(Locator locator) {
		// Don't record this event
	}

	public void ignorableWhitespace(char ch[], int start, int length)
			throws SAXException {
		this.saxbits.add(new IgnorableWhitespace(ch, start, length));
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		this.saxbits.add(new PI(target, data));
	}

	public void startDocument() throws SAXException {
		this.saxbits.add(StartDocument.SINGLETON);
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		this.saxbits
				.add(new StartElement(namespaceURI, localName, qName, atts));
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		this.saxbits.add(new EndPrefixMapping(prefix));
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {
		this.saxbits.add(new Characters(ch, start, length));
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		this.saxbits.add(new EndElement(namespaceURI, localName, qName));
	}

	public void endDocument() throws SAXException {
		this.saxbits.add(EndDocument.SINGLETON);
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		this.saxbits.add(new StartPrefixMapping(prefix, uri));
	}

	//
	// LexicalHandler Interface
	//

	public void endCDATA() throws SAXException {
		this.saxbits.add(EndCDATA.SINGLETON);
	}

	public void comment(char ch[], int start, int length) throws SAXException {
		this.saxbits.add(new Comment(ch, start, length));
	}

	public void startEntity(String name) throws SAXException {
		this.saxbits.add(new StartEntity(name));
	}

	public void endDTD() throws SAXException {
		this.saxbits.add(EndDTD.SINGLETON);
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		this.saxbits.add(new StartDTD(name, publicId, systemId));
	}

	public void startCDATA() throws SAXException {
		this.saxbits.add(StartCDATA.SINGLETON);
	}

	public void endEntity(String name) throws SAXException {
		this.saxbits.add(new EndEntity(name));
	}

	//
	// Public Methods
	//

	/**
	 * @return true if buffer is empty
	 */
	public boolean isEmpty() {
		return this.saxbits.isEmpty();
	}

	/**
	 * @return unmodifiable list of SAX bits
	 */
	public List<SaxBit> getBits() {
		return Collections.unmodifiableList(this.saxbits);
	}

	/**
	 * Stream this buffer into the provided content handler. If contentHandler
	 * object implements LexicalHandler, it will get lexical events as well.
	 */
	public void toSAX(ContentHandler contentHandler) throws SAXException {
		for (SaxBit saxbit : this.saxbits) {
			saxbit.send(contentHandler);
		}
	}

	/**
	 * Dump buffer contents into the provided writer.
	 */
	public void dump(Writer writer) throws IOException {
		Iterator<SaxBit> i = this.saxbits.iterator();
		while (i.hasNext()) {
			final SaxBit saxbit = i.next();
			saxbit.dump(writer);
		}
		writer.flush();
	}

	//
	// Implementation Methods
	//

	/**
	 * Adds a SaxBit to the bits list
	 */
	protected final void addBit(SaxBit bit) {
		this.saxbits.add(bit);
	}

	/**
	 * Iterates through the bits list
	 */
	protected final Iterator<SaxBit> bits() {
		return this.saxbits.iterator();
	}

	/**
	 * SaxBit is a representation of the SAX event. Every SaxBit is immutable
	 * object.
	 */
	public interface SaxBit {
		public void send(ContentHandler contentHandler) throws SAXException;

		public void dump(Writer writer) throws IOException;
	}

	public final static class StartDocument implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431165343331395228L;
		public static final StartDocument SINGLETON = new StartDocument();

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.startDocument();
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[StartDocument]\n");
		}
	}

	public final static class EndDocument implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431165343331395123L;
		public static final EndDocument SINGLETON = new EndDocument();

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.endDocument();
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[EndDocument]\n");
		}
	}

	public final static class PI implements SaxBit, Serializable {
		private static final long serialVersionUID = -443116534333139556L;
		public final String target;
		public final String data;

		public PI(String target, String data) {
			this.target = target;
			this.data = data;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.processingInstruction(this.target, this.data);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[ProcessingInstruction] target=" + this.target
					+ ",data=" + this.data + "\n");
		}
	}

	public final static class StartDTD implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431165343331567228L;
		public final String name;
		public final String publicId;
		public final String systemId;

		public StartDTD(String name, String publicId, String systemId) {
			this.name = name;
			this.publicId = publicId;
			this.systemId = systemId;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).startDTD(this.name,
						this.publicId, this.systemId);
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[StartDTD] name=" + this.name + ",publicId="
					+ this.publicId + ",systemId=" + this.systemId + "\n");
		}
	}

	public final static class EndDTD implements SaxBit, Serializable {
		private static final long serialVersionUID = -4144565343331395228L;
		public static final EndDTD SINGLETON = new EndDTD();

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).endDTD();
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[EndDTD]\n");
		}
	}

	public final static class StartEntity implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431169193331395228L;
		public final String name;

		public StartEntity(String name) {
			this.name = name;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).startEntity(this.name);
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[StartEntity] name=" + this.name + "\n");
		}
	}

	public final static class EndEntity implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431167863331395228L;
		public final String name;

		public EndEntity(String name) {
			this.name = name;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).endEntity(this.name);
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[EndEntity] name=" + this.name + "\n");
		}
	}

	public final static class SkippedEntity implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431165343555395228L;
		public final String name;

		public SkippedEntity(String name) {
			this.name = name;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.skippedEntity(this.name);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[SkippedEntity] name=" + this.name + "\n");
		}
	}

	public final static class StartPrefixMapping implements SaxBit,
			Serializable {
		private static final long serialVersionUID = -4436665343331395228L;
		public final String prefix;
		public final String uri;

		public StartPrefixMapping(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.startPrefixMapping(this.prefix, this.uri);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[StartPrefixMapping] prefix=" + this.prefix + ",uri="
					+ this.uri + "\n");
		}
	}

	public final static class EndPrefixMapping implements SaxBit, Serializable {
		private static final long serialVersionUID = -4499965343331395228L;
		public final String prefix;

		public EndPrefixMapping(String prefix) {
			this.prefix = prefix;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.endPrefixMapping(this.prefix);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[EndPrefixMapping] prefix=" + this.prefix + "\n");
		}
	}

	public final static class StartElement implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431165343377795228L;
		public final String namespaceURI;
		public final String localName;
		public final String qName;
		public final Attributes attrs;

		public StartElement(String namespaceURI, String localName,
				String qName, Attributes attrs) {
			this.namespaceURI = namespaceURI;
			this.localName = localName;
			this.qName = qName;
			this.attrs = new org.xml.sax.helpers.AttributesImpl(attrs);
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.startElement(this.namespaceURI, this.localName,
					this.qName, this.attrs);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[StartElement] namespaceURI=" + this.namespaceURI
					+ ",localName=" + this.localName + ",qName=" + this.qName
					+ "\n");
			for (int i = 0; i < this.attrs.getLength(); i++) {
				writer.write("      [Attribute] namespaceURI="
						+ this.attrs.getURI(i) + ",localName="
						+ this.attrs.getLocalName(i) + ",qName="
						+ this.attrs.getQName(i) + ",type="
						+ this.attrs.getType(i) + ",value="
						+ this.attrs.getValue(i) + "\n");
			}
		}
	}

	public final static class EndElement implements SaxBit, Serializable {
		private static final long serialVersionUID = -4439845343331395228L;
		public final String namespaceURI;
		public final String localName;
		public final String qName;

		public EndElement(String namespaceURI, String localName, String qName) {
			this.namespaceURI = namespaceURI;
			this.localName = localName;
			this.qName = qName;
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.endElement(this.namespaceURI, this.localName,
					this.qName);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[EndElement] namespaceURI=" + this.namespaceURI
					+ ",localName=" + this.localName + ",qName=" + this.qName
					+ "\n");
		}
	}

	public final static class Characters implements SaxBit, Serializable {
		private static final long serialVersionUID = -4438945343331395228L;
		public final char[] ch;

		public Characters(char[] ch, int start, int length) {
			// make a copy so that we don't hold references to a potentially
			// large array we don't control
			this.ch = new char[length];
			System.arraycopy(ch, start, this.ch, 0, length);
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.characters(this.ch, 0, this.ch.length);
		}

		public void toString(StringBuffer value) {
			value.append(this.ch);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[Characters] ch=" + new String(this.ch) + "\n");
		}
	}

	public final static class Comment implements SaxBit, Serializable {
		private static final long serialVersionUID = -4491265343331395228L;
		public final char[] ch;

		public Comment(char[] ch, int start, int length) {
			// make a copy so that we don't hold references to a potentially
			// large array we don't control
			this.ch = new char[length];
			System.arraycopy(ch, start, this.ch, 0, length);
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).comment(this.ch, 0,
						this.ch.length);
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[Comment] ch=" + new String(this.ch) + "\n");
		}
	}

	public final static class StartCDATA implements SaxBit, Serializable {
		private static final long serialVersionUID = -4431165343331347829L;
		public static final StartCDATA SINGLETON = new StartCDATA();

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).startCDATA();
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[StartCDATA]\n");
		}
	}

	public final static class EndCDATA implements SaxBit, Serializable {
		private static final long serialVersionUID = -1111165343331395228L;
		public static final EndCDATA SINGLETON = new EndCDATA();

		public void send(ContentHandler contentHandler) throws SAXException {
			if (contentHandler instanceof LexicalHandler) {
				((LexicalHandler) contentHandler).endCDATA();
			}
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[EndCDATA]\n");
		}
	}

	public final static class IgnorableWhitespace implements SaxBit,
			Serializable {
		private static final long serialVersionUID = -4431194143331395228L;
		public final char[] ch;

		public IgnorableWhitespace(char[] ch, int start, int length) {
			// make a copy so that we don't hold references to a potentially
			// large array we don't control
			this.ch = new char[length];
			System.arraycopy(ch, start, this.ch, 0, length);
		}

		public void send(ContentHandler contentHandler) throws SAXException {
			contentHandler.ignorableWhitespace(this.ch, 0, this.ch.length);
		}

		public void dump(Writer writer) throws IOException {
			writer.write("[IgnorableWhitespace] ch=" + new String(this.ch)
					+ "\n");
		}
	}

	public static boolean isStartElement(SaxBit bit) {
		return bit instanceof StartElement;
	}

	public static boolean isEndElement(SaxBit bit) {
		return bit instanceof EndElement;
	}

	public static boolean isStartDocument(SaxBit bit) {
		return bit instanceof StartDocument;
	}

	public static boolean isEndDocument(SaxBit bit) {
		return bit instanceof EndDocument;
	}

}