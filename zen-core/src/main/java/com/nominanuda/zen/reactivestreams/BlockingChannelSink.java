package com.nominanuda.zen.reactivestreams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

public class BlockingChannelSink extends BlockingSink<ByteBuffer> {
	private final WritableByteChannel ch;

	public BlockingChannelSink(WritableByteChannel _ch) {
		this.ch = _ch;
	}

	@Override
	public void onError(Throwable t) {
		try {
			ch.close();
		} catch (ClosedChannelException e) {
		} catch (IOException e) {
		}
	}

	@Override
	public void onComplete() {
		try {
			ch.close();
		} catch (ClosedChannelException e) {
		} catch (IOException e) {
		}
	}

	@Override
	protected void blockingOp(ByteBuffer t) throws Exception {
		try {
			while(t.hasRemaining()) {
				ch.write(t);
			}
		} catch (Exception e) {
			subscription.cancel();
			try {
				ch.close();
			} catch (ClosedChannelException cce) {
			} catch (IOException ioe) {
			}
		}
	}
}
