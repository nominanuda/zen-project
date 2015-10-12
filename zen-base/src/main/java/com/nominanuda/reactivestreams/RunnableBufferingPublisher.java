package com.nominanuda.reactivestreams;


public abstract class RunnableBufferingPublisher<T> extends AbstractBufferingPublisher<T> implements Runnable {
	protected abstract void work() throws Exception;
	@Override
	public void run() {
		try {
			work();
			completeInternal();
		} catch(Exception e) {
			errorInternal(e);
			throw new RuntimeException(e);
		}
	}
}
