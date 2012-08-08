package com.nominanuda.hyperapi.async;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nominanuda.hyperapi.HyperApi;

public class AsyncTest {

	@Test
	public void test() throws Exception {
		final AtomicInteger testPassed = new AtomicInteger(0);
		Foo foo = new FooImpl();
		SuccessCallback<String> ss = new SuccessCallback<String>() {
			public void apply(String data, String textStatus, Object jqXHR) {
				if("bau".equals(data)||"mm".equals(data)) {
					testPassed.incrementAndGet();
				}
			}
		};
		ErrorCallback ee = null;
		CallbackSerializingAsyncInvoker inv2 = new CallbackSerializingAsyncInvoker();
		inv2.async(foo, ss, ee).f("BAU");
		inv2.async(foo, ss, ee).f("MM");

		inv2.await(1000, TimeUnit.SECONDS);

		assertEquals(2, testPassed.get());
		assertEquals("bau", inv2.getFutures().get(0).get(0, TimeUnit.MILLISECONDS));
		assertEquals("mm", inv2.getFutures().get(1).get(0, TimeUnit.MILLISECONDS));
	}

	@HyperApi
	interface Foo {
		String f(String bau);
	}
	class FooImpl implements Foo {
		public String f(String bau) {
			return bau.toLowerCase();
		}
		
	}
}
