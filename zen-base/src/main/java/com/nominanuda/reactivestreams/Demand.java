package com.nominanuda.reactivestreams;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demand implements Subscription {
	private static final Logger log = LoggerFactory.getLogger(Demand.class);
	private final AtomicLong demand = new AtomicLong(0);
	private final AtomicBoolean canceled = new AtomicBoolean(false);
	private final LinkedList<Runnable> demandListeners = new LinkedList<Runnable>();
	private AtomicBoolean unbounded = new AtomicBoolean(false);
	/**
	 * to be called only by {@link Subscriber}
	 */
	@Override
	public void request(long n) {
		if(! canceled.get()) {
			if(Long.MAX_VALUE == n) {
				unbounded.set(true);
				demand.set(n);
			} else {
				demand.addAndGet(n);
			}
		}
		for(Runnable r : demandListeners) {
			try {
				r.run();
			} catch (Exception e) {
				log.error("callback invocation error", e);
				cancel();
				break;
			}
		}
	}

	/**
	 * to be called only by {@link Subscriber}
	 */
	@Override
	public void cancel() {
		canceled.set(true);
	}

	public boolean isCanceled() {
		return canceled.get();
	}

	/**
	 * decreases demand by 1
	 * @return true if demand has changed e.g. was > 0
	 */
	public boolean unrequest() {
		if(unbounded.get()) {
			return true;
		} else {
			long l = demand.decrementAndGet();
			if(l < 0) {
				demand.incrementAndGet();
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean hasDemand() {
		return demand.get() > 0;
	}

	public long getDemand() {
		return demand.get();
	}

	public boolean isUnbounded() {
		return unbounded.get();

	}
	public void onDemandIncrease(Runnable nonBlockingCallback) {
		demandListeners.add(nonBlockingCallback);
	}
}
