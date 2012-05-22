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

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.nominanuda.lang.Tuple2;
import com.nominanuda.saxpipe.SaxBuffer;
import com.nominanuda.saxpipe.SaxBuffer.SaxBit;
import com.nominanuda.springmvc.DomManipulationInstr;

public class HtmlComposer {
	public enum DomOp {
		prepend, append, before, after, replaceWith, html
	}
	private final ContentHandler target;
	private List<DomManipulationInstr> fragments;
	private final Stack<Tuple2<Integer, Iterator<SaxBit>>> playing = new Stack<Tuple2<Integer,Iterator<SaxBit>>>();
	private final DocContext docContext = new DocContext();
	private int numDocStarted = 0;
	private Logger log = LoggerFactory.getLogger(getClass());
	public HtmlComposer(ContentHandler target) {
		this.target = target;
	}

	public void render(List<DomManipulationInstr> viewMap) throws SAXException {
		fragments = viewMap;
		playing.push(new Tuple2<Integer, Iterator<SaxBit>>(0, viewMap.get(0).getSaxBuffer().getBits().iterator()));
		while(true) {
			Iterator<SaxBit> itr = playing.peek().get1();
			if(itr.hasNext()) {
				SaxBit bit = itr.next();
				if(!itr.hasNext()) {
					playing.pop();
				}
				sendSaxBit(bit);
				if(playing.isEmpty()) {
					break;
				}
			} else {
				playing.pop();
				if(playing.isEmpty()) {
					break;
				} else {
					continue;
				}
			}
		}
	}

	public void sendSaxBit(SaxBit bit) throws SAXException {
		log.debug("sendSaxBit::{}", bit);
		bit.send(docContext);
		if(SaxBuffer.isStartDocument(bit)) {
			if(numDocStarted == 0) {
				target.startDocument();
			}
			numDocStarted++;
		} else if(SaxBuffer.isEndDocument(bit)) {
			if(numDocStarted == 1) {
				target.endDocument();
			}
			numDocStarted--;
		} else if(!on(docContext, bit)) {
			bit.send(target);
		} else {
			docContext.removeLast();
			log.debug("IGNORED");
		}
	}

	private boolean on(DocContext cx, SaxBit curEvent) throws SAXException {
		int insertionCtxOrder = playing.peek().get0();
		for(int i = insertionCtxOrder + 1; i < fragments.size(); i++) {
			String sel = fragments.get(i).getSelector();
			DomOp op = fragments.get(i).getOperation();
			if(cx.matches(sel)) {
				switch (op) {
				case after:
					if(SaxBuffer.isEndElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new PrependingIterator<SaxBit>(curEvent, fragments.get(i).getSaxBuffer().getBits().iterator())));
					}
					return true;
				case prepend:
					if(SaxBuffer.isStartElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new PrependingIterator<SaxBit>(curEvent, fragments.get(i).getSaxBuffer().getBits().iterator())));
					}
					return true;
				case append:
					if(SaxBuffer.isEndElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new AppendingIterator<SaxBit>(
								fragments.get(i).getSaxBuffer().getBits().iterator(), curEvent)));
					}
					return true;
				case before:
					if(SaxBuffer.isStartElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new AppendingIterator<SaxBit>(
								fragments.get(i).getSaxBuffer().getBits().iterator(), curEvent)));
					}
					return true;
				}
			}
		}
		//curEvent.send(target);
		return false;
	}
}
