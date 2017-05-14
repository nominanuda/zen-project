package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;

import com.nominanuda.zen.common.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncCall<API, T> {
	protected final static Logger LOG = LoggerFactory.getLogger(AsyncCall.class);


	/* activity */

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncLoaderInvocationHandler<API, T>(activity, activity.getLoaderManager(), api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(activity, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
		});
	}


	/* fragment */

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncLoaderInvocationHandler<API, T>(fragment.getActivity(), fragment.getLoaderManager(), api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
		});
	}


	/* support fragment */

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncLoaderInvocationHandler<API, T>(fragment.getActivity(), fragment.getActivity().getLoaderManager(), api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
		});
	}


	/* async task */

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncTaskInvocationHandler<API, T>(api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
		});
	}
}
