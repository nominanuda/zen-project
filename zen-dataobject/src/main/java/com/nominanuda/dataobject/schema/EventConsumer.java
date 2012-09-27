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
package com.nominanuda.dataobject.schema;

import java.nio.channels.IllegalSelectorException;
import java.util.Stack;

import com.nominanuda.dataobject.JsonContentHandler;

public abstract class EventConsumer implements JsonContentHandler {
	private final Stack<EventConsumer> stack;
	private ExistentialPredicate predicate;

	public EventConsumer(Stack<EventConsumer> stack) {
		this.stack = stack;
		this.predicate = new ExistentialPredicate();
	}

	public EventConsumer(Stack<EventConsumer> stack, ExistentialPredicate predicate) {
		this.stack = stack;
		this.predicate = predicate;
	}

	public boolean isOptional() {
		return predicate.isOptional();
	}
	public boolean isNullable() {
		return predicate.isNullable();
	}

	@Override
	public void startJSON() throws RuntimeException {
		throw new IllegalStateException();
	}

	@Override
	public void endJSON() throws RuntimeException {
		throw new IllegalStateException();
	}

	@Override
	public boolean startObject() throws RuntimeException {
		throw new ValidationException("unespected start of object");
	}

	@Override
	public boolean endObject() throws RuntimeException {
		throw new ValidationException("unespected end of object");
	}

	@Override
	public boolean startObjectEntry(String key) throws RuntimeException {
		throw new ValidationException("unespected start of object entry with key:"+key);
	}

	@Override
	public boolean endObjectEntry() throws RuntimeException {
		throw new ValidationException("unespected end of object entry");
	}

	@Override
	public boolean startArray() throws RuntimeException {
		throw new ValidationException("unespected start of array");
	}

	@Override
	public boolean endArray() throws RuntimeException {
		throw new ValidationException("unespected end of array");
	}

	@Override
	public boolean primitive(Object value) throws RuntimeException {
		throw new ValidationException("unespected primitive value "+value);
	}

	protected void pop() {
		stack.pop();
	}
	protected void push(EventConsumer c) {
		stack.push(c);
	}

	public void setPredicate(ExistentialPredicate predicate) {
		this.predicate = predicate;
	}
}
