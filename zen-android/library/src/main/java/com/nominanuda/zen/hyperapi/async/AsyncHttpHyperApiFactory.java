package com.nominanuda.zen.hyperapi.async;

import com.nominanuda.zen.hyperapi.HyperApiFactory;

import java.lang.reflect.Proxy;

/**
 * Created by azum on 20/03/17.
 */

public class AsyncHttpHyperApiFactory implements HyperApiFactory {
	@Override
	public <T> T getInstance(String instanceHint, Class<? extends T> apiInterface) {
		return apiInterface.cast(Proxy.newProxyInstance(apiInterface.getClassLoader(), new Class[] { apiInterface },
				new AsyncHyperApiHttpInvocationHandler()));
	}
}
