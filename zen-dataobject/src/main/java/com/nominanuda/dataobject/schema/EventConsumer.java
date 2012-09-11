package com.nominanuda.dataobject.schema;

import java.util.Stack;

import com.nominanuda.dataobject.JsonContentHandler;

public abstract class EventConsumer implements JsonContentHandler {
	private final Stack<EventConsumer> stack;

	public EventConsumer(Stack<EventConsumer> stack) {
		this.stack = stack;
	}

	@Override
	public void startJSON() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void endJSON() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean startObject() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean endObject() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean startObjectEntry(String key) throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean endObjectEntry() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean startArray() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean endArray() throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean primitive(Object value) throws RuntimeException {
		throw new UnsupportedOperationException();
	}

	protected void pop() {
		stack.pop();
	}
	protected void push(EventConsumer c) {
		stack.push(c);
	}
}
