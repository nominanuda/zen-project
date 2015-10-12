package com.nominanuda.reactivestreams;

import static com.nominanuda.lang.Check.notNull;

import org.reactivestreams.Subscriber;

public class ReactiveStreams {

	public static boolean isFloodable(Subscriber<? super Object> subscriber) {
		return notNull(subscriber) instanceof Floodable;
	}

	public static boolean isSubscribable(Object o) {
		return o != null && (o instanceof Subscriber || o instanceof SubscriberSemantics);
	}
}
