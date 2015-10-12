package com.nominanuda.reactivestreams;

import org.reactivestreams.Publisher;

public interface DemandInjectablePublisher<T> extends Publisher<T> {
	void setSubscription(Demand sImpl);
}
