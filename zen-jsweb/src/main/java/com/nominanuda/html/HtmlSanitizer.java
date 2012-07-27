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
package com.nominanuda.html;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TransformerHandler;

import nu.validator.htmlparser.sax.HtmlParser;

import org.xml.sax.InputSource;

import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.xml.HtmlFragmentParser;
import com.nominanuda.xml.HtmlPurifyTransformer;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.TextSelectTransformer;
import com.nominanuda.xml.WhiteSpaceNormalizingTransformer;
import com.nominanuda.xml.XHtml5Serializer;

public class HtmlSanitizer {

	public String cleanHtml(Reader r) {
		CharArrayWriter caw = new CharArrayWriter();
		cleanHtml(r, caw);
		return caw.toString();
	}
	
	public String htmlToText(Reader r) {
		CharArrayWriter caw = new CharArrayWriter();
		htmlToText(r, caw);
		return caw.toString();
	}

	public void htmlToText(Reader source, Writer sink) {
		runHtmlPipeline(source, sink,
			new WhiteSpaceNormalizingTransformer(),
			new TextSelectTransformer());
	}
	public void cleanHtml(Reader source, Writer sink) {
		runHtmlPipeline(source, sink,
			new WhiteSpaceNormalizingTransformer(),
			new HtmlPurifyTransformer());
	}

	public void runHtmlPipeline(Reader source, Writer sink, TransformerHandler... tx) {
		HtmlParser parser = new HtmlParser();
		parser.setMappingLangToXmlLang(true);
		parser.setReportingDoctype(false);
		SAXSource src = new SAXSource(new HtmlFragmentParser(parser), new InputSource(source));
		SAXResult snk = new SAXResult(new XHtml5Serializer(sink));
		SAXPipeline pipe = new SAXPipeline();
		for(TransformerHandler t : tx) {
			pipe.add(new InstanceFactory<TransformerHandler>(t));
		}
		pipe.complete().build(src, snk).run();
	}}
