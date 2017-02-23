package com.nominanuda.zen.reactivestreams;

import java.io.IOException;
import java.io.Writer;

public class BlockingWriterSink extends BlockingSink<String> {
	private final Writer w;

	public BlockingWriterSink(Writer w) {
		this.w = w;
	}

	@Override
	public void onError(Throwable t) {
		try {
			w.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void onComplete() {
		try {
			w.close();
		} catch (IOException e) {
		}
	}

	@Override
	protected void blockingOp(String t) throws Exception {
		try {
			w.write(t);
		} catch (Exception e) {
			subscription.cancel();
		}
	}
}
