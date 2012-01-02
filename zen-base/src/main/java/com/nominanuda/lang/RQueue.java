package com.nominanuda.lang;

import java.util.concurrent.TimeUnit;

import com.nominanuda.code.Nullable;

public interface RQueue<T> {

	@Nullable T poll(long timeout, TimeUnit unit) throws InterruptedException;

	@Nullable T peek();

}
