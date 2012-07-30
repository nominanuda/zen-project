package com.nominanuda.dataobject;

import org.antlr.runtime.RecognitionException;

public class WrappingRecognitionException extends RecognitionException {
	private static final long serialVersionUID = -6627027944573163753L;
	private final Exception wrapped;
	public WrappingRecognitionException(Exception wrapped) {
		super();
		this.wrapped = wrapped;
	}
	public Exception getWrappedException() {
		return wrapped;
	}
	
}
