package com.nominanuda.web.http.concurrent;

import java.util.concurrent.CountDownLatch;

import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FutureCallbackLatch<T> extends CountDownLatch implements FutureCallback<T> {
	private final static Logger LOG = LoggerFactory.getLogger(FutureCallbackLatch.class);
	
	public FutureCallbackLatch(int count) {
		super(count);
	}

	@Override
	public final void completed(T result) {
		countDown();
		LOG.info("job completed, {} remaining", getCount());
	}

	@Override
	public final void failed(Exception ex) {
		countDown();
		LOG.info("job failed, {} remaining", getCount());
	}

	@Override
	public final void cancelled() {
		countDown();
		LOG.info("job cancelled, {} remaining", getCount());
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
