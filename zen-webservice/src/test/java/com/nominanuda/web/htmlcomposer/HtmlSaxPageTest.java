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

import static com.nominanuda.web.htmlcomposer.DomOp.after;
import static com.nominanuda.web.htmlcomposer.DomOp.append;
import static com.nominanuda.web.htmlcomposer.DomOp.before;
import static com.nominanuda.web.htmlcomposer.DomOp.html;
import static com.nominanuda.web.htmlcomposer.DomOp.prepend;
import static com.nominanuda.web.htmlcomposer.DomOp.replaceWith;
import static org.junit.Assert.assertEquals;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import nu.validator.htmlparser.sax.HtmlParser;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.io.ReaderInputStream;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.lang.ObjectFactory;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.xml.HtmlFragmentParser;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.SaxBuffer;
import com.nominanuda.xml.XHtml5Serializer;

public class HtmlSaxPageTest implements HttpProtocol {
	static {
		BasicConfigurator.configure();
	}
	@Test
	public void test() throws SAXException {
		LinkedList<DomManipulationStmt> stmts = new LinkedList<DomManipulationStmt>();
		stmts.add(new DomManipulationStmt("body", f("<div/>"), html));
		stmts.add(new DomManipulationStmt("div", f("abc"), html));
		stmts.add(new DomManipulationStmt("div", f("0<p>1</p>1.5<p>2</p>3"), replaceWith));
		stmts.add(new DomManipulationStmt("p", f("BEFOREP"), before));
		stmts.add(new DomManipulationStmt("p", f("AFTERP"), after));
		stmts.add(new DomManipulationStmt("p", f("PREPENDP"), prepend));
		stmts.add(new DomManipulationStmt("p", f("APPENDP"), append));
		streamPage(stmts);
	}

	private void streamPage(Iterable<DomManipulationStmt> stmts)
			throws SAXException {
		HtmlSaxPage p = new HtmlSaxPage();
		for(DomManipulationStmt stmt : stmts) {
			p.applyStmt(stmt);
		}
		CharArrayWriter out = new CharArrayWriter();
		XHtml5Serializer ser = new XHtml5Serializer(out);
		new SaxBuffer.StartDocument().send(ser);
		new SaxBuffer.StartElement(HTMLNS,"html","html",new AttributesImpl()).send(ser);
		p.toSAX(ser);
		new SaxBuffer.EndElement(HTMLNS,"html","html").send(ser);
		new SaxBuffer.EndDocument().send(ser);
		assertEquals("<html><head></head><body>0BEFOREP<p>PREPENDP1APPENDP</p>AFTERP1.5BEFOREP<p>PREPENDP2APPENDP</p>AFTERP3</body></html>", out.toString());
	}

	private ObjectFactory<SaxBuffer> f(String s) {
		InputStream is = new ReaderInputStream(new StringReader(s), UTF_8);
		SaxBuffer sbuf = new SaxBuffer();
		new SAXPipeline()
			.complete()
			.build(saxSource(is), new SAXResult(sbuf))
			.run();
		return new InstanceFactory<SaxBuffer>(sbuf);
	}

	private Source saxSource(InputStream is) {
		HtmlParser parser = new HtmlParser();
		parser.setMappingLangToXmlLang(true);
		parser.setReportingDoctype(false);
		SAXSource src = new SAXSource(new HtmlFragmentParser(parser), new InputSource(is));
		return src;
	}
}
