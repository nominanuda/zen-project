package com.nominanuda.reactivestreams;

import static com.nominanuda.lang.Check.notNull;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.ObjectFactory;

public abstract class Accumulator<T,R> implements Subscriber<T>, ObjectFactory<R> {
	private AtomicBoolean completed = new AtomicBoolean(false);
	private R result;
	private Throwable exc;
	
	@Override
	public final void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public final void onError(Throwable t) {
		completed.set(true);
		exc = notNull(t);
	}

	@Override
	public final @Nullable R getObject() {
		if(exc != null) {
			throw new RuntimeException(exc);
		} else if(completed.get()) {
			return result;
		} else {
			throw new IllegalStateException("result not set");
		}
	}

	protected final void setResult(R res) {
		if(completed.compareAndSet(false, true)) {
			result = res;
		}
	}
}
