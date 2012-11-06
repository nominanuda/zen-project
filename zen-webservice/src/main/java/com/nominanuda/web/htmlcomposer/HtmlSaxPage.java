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

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.xml.SAXEmitter;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.SaxBuffer;

public class HtmlSaxPage implements SAXEmitter, HttpProtocol {
	private static final String BODY = "body";
	private static final String HEAD = "head";
	private final List<DomManipulationStmt> stmts = new LinkedList<DomManipulationStmt>();

	public void applyStmt(DomManipulationStmt domManipulationStmt) {
		stmts.add(domManipulationStmt);
	}

	public void toSAX(ContentHandler ch) throws SAXException {
		ContentHandler x = buildPipe(ch);
		new SaxBuffer.StartElement(HTMLNS, HEAD, HEAD, new AttributesImpl()).send(x);
		new SaxBuffer.EndElement(HTMLNS, HEAD, HEAD).send(x);
		new SaxBuffer.StartElement(HTMLNS, BODY, BODY, new AttributesImpl()).send(x);
		new SaxBuffer.EndElement(HTMLNS, BODY, BODY).send(x);
	}

	private ContentHandler buildPipe(ContentHandler ch) {
		SAXPipeline p = new SAXPipeline();
		for(DomManipulationStmt stmt : stmts) {
			TransformerHandler instance = buildHandler(stmt);
			p.add(new InstanceFactory<TransformerHandler>(instance));
		}
		ContentHandler x = p.complete().build(new SAXResult(ch)).getHandler();
		return x;
	}

	private TransformerHandler buildHandler(DomManipulationStmt stmt) {
		return DomManipulationHandler.build(stmt);
	}
}
