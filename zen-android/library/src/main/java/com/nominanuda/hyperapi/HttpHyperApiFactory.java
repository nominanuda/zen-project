package com.nominanuda.hyperapi;

import java.lang.reflect.Proxy;

import okhttp3.OkHttpClient;

import static com.nominanuda.zen.io.Uris.URIS;

/**
 * Created by azum on 27/03/17.
 */

public class HttpHyperApiFactory implements HyperApiFactory {
	private final String uriPrefix;
	private final OkHttpClient okHttpClient;


	public HttpHyperApiFactory(String prefix, OkHttpClient client) {
		uriPrefix = prefix;
		okHttpClient = client;
	}
	public HttpHyperApiFactory(OkHttpClient client) {
		this(null, client);
	}

	@Override
	public <T> T getInstance(String instanceHint, Class<? extends T> apiInterface) {
		return apiInterface.cast(Proxy.newProxyInstance(
			apiInterface.getClassLoader(), new Class[] { apiInterface },
			new HyperApiHttpInvocationHandler(okHttpClient, URIS.pathJoin(uriPrefix, instanceHint))
		));
	}
}
