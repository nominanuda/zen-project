package com.nominanuda.reactivestreams;

import org.reactivestreams.Subscriber;

/**
 * marker interface. Signals semantics of a {@link Subscriber}
 * that will signal unbounded demand, so publishing to it can be done
 * effectively as {@link Subscription#request(Long#MAX_VALUE)} was called
 *
 */
public interface Floodable {

}
