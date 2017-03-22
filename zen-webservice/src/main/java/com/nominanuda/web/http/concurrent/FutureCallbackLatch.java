package com.nominanuda.web.http.concurrent;

import java.util.concurrent.CountDownLatch;

import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;

public class FutureCallbackLatch<T> extends CountDownLatch implements FutureCallback<T> {
	private final Logger log;
	private final String logMessage;
	
	public FutureCallbackLatch(int count, Logger log, String logMessage) {
		super(count);
		this.log = log;
		this.logMessage = logMessage;
	}
	public FutureCallbackLatch(int count, Logger log) {
		this(count, log, "job %s, %d remaining");
	}
	public FutureCallbackLatch(int count) {
		this(count, null, null);
	}

	@Override
	public final void completed(T result) {
		countDown();
		if (log != null) {
			String.format(logMessage, "completed", getCount());
		}
	}

	@Override
	public final void failed(Exception ex) {
		countDown();
		if (log != null) {
			String.format(logMessage, "failed", getCount());
		}
	}

	@Override
	public final void cancelled() {
		countDown();
		if (log != null) {
			String.format(logMessage, "cancelled", getCount());
		}
	}
	
	
	public FutureCallback<T> wrap(final FutureCallback<T> cback) {
		return new FutureCallback<T>() {
			@Override
			public void completed(T result) {
				cback.completed(result);
				FutureCallbackLatch.this.completed(result);
			}

			@Override
			public void failed(Exception ex) {
				cback.failed(ex);
				FutureCallbackLatch.this.failed(ex);
			}

			@Override
			public void cancelled() {
				cback.cancelled();
				FutureCallbackLatch.this.cancelled();
			}
		};
	}
}
