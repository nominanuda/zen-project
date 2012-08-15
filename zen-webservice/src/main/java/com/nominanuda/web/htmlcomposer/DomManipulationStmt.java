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
