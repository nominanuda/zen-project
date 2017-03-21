/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.reactivestreams;

import static com.nominanuda.zen.common.Check.notNull;
import static com.nominanuda.zen.reactivestreams.ReactiveUtils.cappedSum;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;

//TODO only one in one out functions supported
public class BufferingProcessor<T,R> extends TransformingProcessor<T, R> {
	public static final int DEFAULT_RING_BUFFER_SIZE = 1024;
	private Subscriber<? super R> subscriber;
	private RingBuffer<ReactiveEvent<R>> ringBuffer;
	private boolean syncMode = true;
	private Executor workerPool;
	private int bufferSize = DEFAULT_RING_BUFFER_SIZE;

	@Override
	public final void onSubscribe(Subscription s) {
		if(ringBuffer == null) {
			ringBuffer = RingBuffer.createSingleProducer(new EventFactory<ReactiveEvent<R>>() {
				@Override
				public ReactiveEvent<R> newInstance() {
					return new ReactiveEvent<R>();
				}
			}, bufferSize);
		}
		this.upstreamSubscription = s;
	}

	@Override
	public final void subscribe(Subscriber<? super R> s) {
		if(s == null) {
			throw new NullPointerException("rule 1.9");
		} else if(subscriber != null) {
			s.onSubscribe(new SubscriptionImpl());
			s.onError(new IllegalStateException("this publisher does not allow multiple subscribers"));
		} else {
			subscriber = s;
			subscriber.onSubscribe(downstreamSubscription);
		}
	}

	private volatile long last = -1;
	private volatile long maxPublished = -1;

	public final void onNextOrComplete(final @Nullable /*null means complete*/ T t) {
		final long claimed = ringBuffer.next();
		//TODO avoid new Runnable()
		if(syncMode) {
			try {
				if(t == null) {
					ringBuffer.get(claimed).complete();
				} else {
					//potentially blocking
					R r = getFunction().apply(t);
					//
					ringBuffer.get(claimed).set(r);
				}
				ringBuffer.publish(claimed);
				if(claimed > maxPublished) {//TODO race cond
					maxPublished = claimed;
				}
				drain();
			} catch(Throwable e) {//we risk not to publish a claimed seq but in this case the whole RingBuffer is invalid and not used anymore
				onError(e);
			}
		} else {
			workerPool.execute(() -> {
				try {
					if(t == null) {
						ringBuffer.get(claimed).complete();
					} else {
						//potentially blocking
						R r = getFunction().apply(t);
						//
						ringBuffer.get(claimed).set(r);
					}
					ringBuffer.publish(claimed);
					if(claimed > maxPublished) {//TODO race cond
						maxPublished = claimed;
					}
					drain();
				} catch(Throwable e) {//we risk not to publish a claimed seq but in this case the whole RingBuffer is invalid and not used anymore
					onError(e);
				}
			});
		}
	}

	private final AtomicBoolean drainGuard = new AtomicBoolean(true);
	private void drain() {
		if(drainGuard.compareAndSet(true, false)) {
			for(long z  = last + 1; z <= maxPublished/*cur*/; z++) {
				ReactiveEvent<R> ar = ringBuffer.get(z);
				if(ar.isSet()) {
					if(! ar.isComplete()) {
						downDem--;
					}
					ar.flushTo(subscriber);
					last = z;
				} else {
					break;
				}
			}
			updateUpstreamSubscription();
			drainGuard.set(true);
		}
	}

	/**
	 * @see {@link Processor#onNext(Object)}
	 */
	@Override
	public final void onNext(final T t) {
		upDem--;
		onNextOrComplete(notNull(t));
	}

	/**
	 * @see {@link Processor#onComplete()}
	 */
	@Override
	public final void onComplete() {
		onNextOrComplete(null);
	}

	boolean upstreamCompleted = false;
	@Override
	protected void out(R r) {
		throw new UnsupportedOperationException();
		
	}

	/**
	 * @see {@link Processor#onError(Throwable)}
	 */
	@Override
	public final void onError(Throwable t) {
			subscriber.onError(t);
			//TODO
	}

	///// DEMAND MATHS AND BUFFER FLUSHING
	private volatile long downDem = 0;
	private volatile long upDem = 0;
	private boolean downUnbounded = false;
	private final Consumer<Long> onDownstreamSubReq = (n) -> {
		if(! downUnbounded) {
			downDem = cappedSum(downDem, n);
			if(downDem == Long.MAX_VALUE) {
				downUnbounded = true;
			}
		}
		updateUpstreamSubscription();
	};
	private Subscription upstreamSubscription;
	private DelegatingSubscription downstreamSubscription = new DelegatingSubscription();
	{
		downstreamSubscription.onRequest(onDownstreamSubReq);
	}


	//!!!!! at all times 
	//downDem <= Long.MAX_VALUE; see onDownstreamSubReq
	//upDem <= downDem - busySlots(aka bufferSize - freeSlots) 
	//upDem <= freeSlots
	//upDem <= Long.MAX_VALUE; see upstreamRequest()
	private final AtomicBoolean updateUpstreamSubscriptionGuard = new AtomicBoolean(true);
	private void updateUpstreamSubscription() {
		if(updateUpstreamSubscriptionGuard.compareAndSet(true, false)) {
			long busySlots = maxPublished - last + 1;/*room for onComplete*/
			long freeSlots = bufferSize - busySlots;
			long targetUpDem = downUnbounded
				? freeSlots
				: min(max(downDem - busySlots, 0), freeSlots);
			long deltaUpDem = targetUpDem - upDem;
			if(deltaUpDem > 0) {
				upstreamRequest(deltaUpDem);
			} else {
				System.err.println("PANIC 11");
			}
			updateUpstreamSubscriptionGuard.set(true);
		}
	}
	private void upstreamRequest(long n) {
		if(upstreamSubscription != null) {
			upstreamSubscription.request(n);
		} else {
			System.err.println("PANIC 22");
		}
	}



	//configuration
	/**
	 * the {@link Executor} or {@link ExecutorService} to run the transformation tasks
	 * if it allows parallel executions, reordering of results is managed by this class.
	 * {@link Executor#execute(Runnable)} is called in the context of {@link #onNext(Object)}
	 * and the default is to do so on the same {@link Thread}, so call this method if the 
	 * applied transformation is blocking, or parallel executions are desired.
	 * 
	 * @param workerPool
	 */
	public final void setWorkerPool(Executor workerPool) {
		this.workerPool = workerPool;
		this.syncMode = false;
	}
	/**
	 * the {@link RingBuffer} buffer size. Shoud be a power of 2. The default is {@value #DEFAULT_RING_BUFFER_SIZE}
	 * @param bufferSize
	 */
	public final void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
