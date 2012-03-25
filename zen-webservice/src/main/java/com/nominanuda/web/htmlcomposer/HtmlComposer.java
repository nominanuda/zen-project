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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.nominanuda.lang.Tuple2;
import com.nominanuda.lang.Tuple3;
import com.nominanuda.saxpipe.SaxBuffer;
import com.nominanuda.saxpipe.SaxBuffer.SaxBit;

public class HtmlComposer {
	public enum DomOp {
		prepend, append, before, after, replaceWith, html
	}
	private final ContentHandler target;
	private List<Tuple3<String, SaxBuffer, DomOp>> fragments;
	private final Stack<Tuple2<Integer, Iterator<SaxBit>>> playing = new Stack<Tuple2<Integer,Iterator<SaxBit>>>();
	private final DocContext docContext = new DocContext();
	private int numDocStarted = 0;

	public HtmlComposer(ContentHandler target) {
		this.target = target;
	}

	public void render(List<Tuple3<String, SaxBuffer, DomOp>> viewMap) throws SAXException {
		fragments = viewMap;
		playing.push(new Tuple2<Integer, Iterator<SaxBit>>(0, viewMap.get(0).get1().getBits().iterator()));
		while(true) {
			Iterator<SaxBit> itr = playing.peek().get1();
			if(itr.hasNext()) {
				SaxBit bit = itr.next();
				sendSaxBit(bit);
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
		bit.send(docContext);
		if(SaxBuffer.isStartDocument(bit) && numDocStarted == 0) {
			numDocStarted++;
			target.startDocument();
		} else if(SaxBuffer.isEndDocument(bit) && numDocStarted == 1) {
			numDocStarted--;
			target.endDocument();
		} else if(!on(docContext, bit)) {
			bit.send(target);
		}
	}

	private boolean on(DocContext cx, SaxBit curEvent) throws SAXException {
		int insertionCtxOrder = playing.peek().get0();
		for(int i = insertionCtxOrder + 1; i < fragments.size(); i++) {
			String sel = fragments.get(i).get0();
			DomOp op = fragments.get(i).get2();
			if(cx.matches(sel)) {
				switch (op) {
				case after:
					if(SaxBuffer.isEndElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new PrependingIterator<SaxBit>(curEvent, fragments.get(i).get1().getBits().iterator())));
					}
					break;
				case prepend:
					if(SaxBuffer.isStartElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new PrependingIterator<SaxBit>(curEvent, fragments.get(i).get1().getBits().iterator())));
					}
					break;
				case append:
					if(SaxBuffer.isEndElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new AppendingIterator<SaxBit>(
								fragments.get(i).get1().getBits().iterator(), curEvent)));
					}
					break;
				case before:
					if(SaxBuffer.isStartElement(curEvent)) {
						playing.push(new Tuple2<Integer, Iterator<SaxBit>>(i, new AppendingIterator<SaxBit>(
								fragments.get(i).get1().getBits().iterator(), curEvent)));
					}
					break;
				}
				return true;
			}
		}
		//curEvent.send(target);
		return false;
	}
}
