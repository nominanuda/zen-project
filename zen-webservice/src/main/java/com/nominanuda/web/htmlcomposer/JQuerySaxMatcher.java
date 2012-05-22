package com.nominanuda.web.htmlcomposer;

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.lang.Strings;
import com.nominanuda.saxpipe.SAXPipeline;
import com.nominanuda.saxpipe.SaxBuffer;
import com.nominanuda.saxpipe.SaxBuffer.SaxBit;
import com.nominanuda.saxpipe.SaxBuffer.StartElement;
import com.nominanuda.web.htmlcomposer.HtmlComposer.DomOp;

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
