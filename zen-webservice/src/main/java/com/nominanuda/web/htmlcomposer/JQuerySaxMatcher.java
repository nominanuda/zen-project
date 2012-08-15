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

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.lang.Strings;
import com.nominanuda.web.htmlcomposer.DomOp;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.SaxBuffer;
import com.nominanuda.xml.SaxBuffer.SaxBit;
import com.nominanuda.xml.SaxBuffer.StartElement;

public class JQuerySaxMatcher {
	private final DomOp operation;
	private String selector;
	private SaxBuffer saxBuf;

	public JQuerySaxMatcher(DomManipulationStmt domManipulationStmt) {
		operation = domManipulationStmt.getOperation();
		selector = domManipulationStmt.getSelector();
		saxBuf = new SaxBuffer();
		new SAXPipeline()
			.add(new InstanceFactory<TransformerHandler>(new HtmlFragmentChecker()))
			.complete()
			.build(domManipulationStmt.getSaxBuffer(), new SAXResult(saxBuf))
			.run();
	}

	public int match(LinkedList<SaxBit> bits, int i) {
		SaxBit bit = bits.get(i);
		List<SaxBit> sbl = saxBuf.getBits();
		if(SaxBuffer.isStartElement(bit) && selector.startsWith(".") && operation == DomOp.prepend) {
			StartElement se = (StartElement)bit;
//			String cls = se.attrs.getValue("class");
			if(classMatches(se.attrs.getValue("class"), selector.substring(1))) {
				int added = 0;
				for(int j = sbl.size() - 1; j >= 0; j--) {
					SaxBit bb = clean(sbl.get(j));
					if(isRelevant(bb)) {
						bits.add(i+1, bb);
						added++;
					}
				}
				return i + added;
			}
		} else if(SaxBuffer.isStartElement(bit) && operation == DomOp.html) {
			StartElement se = (StartElement)bit;
			if(se.localName.equals(selector)) {
				int added = 0;
				for(int j = sbl.size() - 1; j >= 0; j--) {
					SaxBit bb = clean(sbl.get(j));
					if(isRelevant(bb)) {
						bits.add(i+1, bb);
						added++;
					}
				}
				return i + added;
			}
		}
		return i+1;
	}

	private boolean classMatches(@Nullable String clsAttr, String targetClass) {
		return clsAttr != null &&  Strings.splitAndTrim(clsAttr, "\\s+").contains(targetClass);
	}

	private SaxBit clean(SaxBit saxBit) {
		return saxBit;
	}

	private boolean isRelevant(SaxBit bb) {
		
		return ! (SaxBuffer.isStartDocument(bb)||SaxBuffer.isEndDocument(bb));
	}
}
