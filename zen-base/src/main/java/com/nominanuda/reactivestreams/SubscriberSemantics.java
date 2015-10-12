package com.nominanuda.reactivestreams;

import org.reactivestreams.Subscriber;

/**
 * Marker interface for object that can get and signal appropriately a
 * {@link Subscription} implementors MUST implement a method with same signature
 * as {@link Subscriber#onSubscribe(Subscription)}
 */
public interface SubscriberSemantics {
}
