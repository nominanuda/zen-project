package com.nominanuda.web.htmlcomposer;

import com.nominanuda.code.Immutable;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.ObjectFactory;
import com.nominanuda.xml.SaxBuffer;

@Immutable
public class DomManipulationStmt {
	final String selector;
	final ObjectFactory<SaxBuffer> saxBufferFactory;
	final DomOp operation;
	SaxBuffer saxBuffer;

	public DomManipulationStmt(String selector, ObjectFactory<SaxBuffer> saxBufferFactory,
			DomOp operation) {
		this.selector = Check.notNull(selector);
		this.saxBufferFactory = Check.notNull(saxBufferFactory);
		this.operation = Check.notNull(operation);
	}

	public String getSelector() {
		return selector;
	}

	public synchronized SaxBuffer getSaxBuffer() {
		if(saxBuffer == null) {
			saxBuffer = Check.notNull(saxBufferFactory.getObject());
		}
		return saxBuffer;
	}

	public DomOp getOperation() {
		return operation;
	}
}
