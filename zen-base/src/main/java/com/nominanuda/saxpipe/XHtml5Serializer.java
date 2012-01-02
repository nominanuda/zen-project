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
package com.nominanuda.saxpipe;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import nu.validator.htmlparser.sax.HtmlSerializer;

import org.xml.sax.SAXException;

import com.nominanuda.lang.ReflectionHelper;

public class XHtml5Serializer extends HtmlSerializer implements HtmlConstants {
		private static final String[] VOID_ELEMENTS = { area, base,
				basefont, bgsound, br, col, embed, frame, hr,
				img, input, link, meta, param, spacer, wbr };
		private final XHtml5Serializer.Wr wr;
		private final static ReflectionHelper reflect = new ReflectionHelper();
		private boolean outputDoctype = false;

		public XHtml5Serializer(Writer out) {
			super(new Wr(out));
			wr = (XHtml5Serializer.Wr)reflect.getFieldValueIncludingAncestors("writer", this, true);
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes atts) throws SAXException {
			if(Arrays.binarySearch(VOID_ELEMENTS, localName) > -1) {
				wr.beginElementFixMode();
				super.startElement(uri, localName, qName, atts);
				try {
					wr.endElementFixMode();
				} catch (IOException e) {
					throw new SAXException(e);
				}
			} else {
				super.startElement(uri, localName, qName, atts);
			}
			
		}

		public void startDocument() throws SAXException {
			if(outputDoctype) {
				try {
					wr.write("<!DOCTYPE html>\n");
				} catch (IOException e) {
					throw new SAXException(e);
				}
			}
		}

		private static class Wr extends Writer {
			private Writer delegee;
			private boolean fixMode = false;
			private StringBuilder fixBuf;

			public Wr(Writer w) {
				this.delegee = w;
			}
			public void beginElementFixMode() {
				fixMode = true;
				fixBuf = new StringBuilder();
			}
			public void endElementFixMode() throws IOException {
				fixMode = false;
				String s = fixBuf.toString().replaceAll("<([^>]+)>", "<$1/>");
				delegee.write(s.toCharArray(), 0, s.length());
			}
			public void write(char[] cbuf, int off, int len) throws IOException {
				if(fixMode) {
					fixBuf.append(new String(cbuf, off, len));
				} else {
					delegee.write(cbuf, off, len);
				}
			}

			@Override
			public void flush() throws IOException {
				delegee.flush();
			}

			@Override
			public void close() throws IOException {
				delegee.close();
			}

			@Override
			public void write(int c) throws IOException {
				write(new char[] {(char)c});
			}

			@Override
			public void write(char[] cbuf) throws IOException {
				write(cbuf, 0, cbuf.length);
			}

			@Override
			public void write(String str) throws IOException {
				write(str.toCharArray());
			}

			@Override
			public void write(String str, int off, int len) throws IOException {
				write(str.toCharArray(), off, len);
			}

			@Override
			public Writer append(CharSequence csq) throws IOException {
				return super.append(csq);
			}

			@Override
			public Writer append(CharSequence csq, int start, int end)
					throws IOException {
				write(csq.subSequence(start, end).toString());
				return this;
			}

			@Override
			public Writer append(char c) throws IOException {
				write(c);
				return this;
			}
		}

		public boolean isOutputDoctype() {
			return outputDoctype;
		}
		public void setOutputDoctype(boolean outputDoctype) {
			this.outputDoctype = outputDoctype;
		}
	}