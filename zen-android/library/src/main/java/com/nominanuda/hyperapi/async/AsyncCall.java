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

	// main
	static <API, T> API proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		return apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncLoaderInvocationHandler<>(activity, activity.getLoaderManager(), api, resultFnc, errorFnc, finalFnc)
		));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		callFnc.apply(proxy(activity, api, resultFnc, errorFnc, finalFnc));
	}


	// no finalFnc
	static <API, T> API proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(activity, api, resultFnc, errorFnc, () -> {
			// do nothing
		});
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		callFnc.apply(proxy(activity, api, resultFnc, errorFnc));
	}


	// no errorFnc
	static <API, T> API proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(activity, api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		callFnc.apply(proxy(activity, api, resultFnc, finalFnc));
	}


	// just resultFnc
	static <API, T> API proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc) {
		return proxy(activity, api, resultFnc, e -> LOG.error("errorFnc", e));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		callFnc.apply(proxy(activity, api, resultFnc));
	}



	/* fragment */

	// main
	static <API, T> API proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		return apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncLoaderInvocationHandler<>(fragment.getActivity(), fragment.getLoaderManager(), api, resultFnc, errorFnc, finalFnc)
		));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}


	// no finalFnc
	static <API, T> API proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(fragment, api, resultFnc, errorFnc, () -> {
			// do nothing
		});
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc, errorFnc));
	}


	// no errorFnc
	static <API, T> API proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(fragment, api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc, finalFnc));
	}


	// just resultFnc
	static <API, T> API proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc) {
		return proxy(fragment, api, resultFnc, e -> LOG.error("errorFnc", e));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc));
	}



	/* support fragment */

	// main
	static <API, T> API proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		return apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncLoaderInvocationHandler<>(fragment.getActivity(), fragment.getActivity().getLoaderManager(), api, resultFnc, errorFnc, finalFnc)
		));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}


	// no finalFnc
	static <API, T> API proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(fragment, api, resultFnc, errorFnc, () -> {
			// do nothing
		});
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc, errorFnc));
	}


	// no errorFnc
	static <API, T> API proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(fragment, api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc, finalFnc));
	}


	// just resultFnc
	static <API, T> API proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc) {
		return proxy(fragment, api, resultFnc, e -> LOG.error("errorFnc", e));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		callFnc.apply(proxy(fragment, api, resultFnc));
	}



	/* async task */

	// main
	static <API, T> API proxy(final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		return apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncTaskInvocationHandler<>(api, resultFnc, errorFnc, finalFnc)
		));
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		callFnc.apply(proxy(api, resultFnc, errorFnc, finalFnc));
	}


	// no finalFnc
	static <API, T> API proxy(final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(api, resultFnc, errorFnc, () -> {
			// do nothing
		});
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		callFnc.apply(proxy(api, resultFnc, errorFnc));
	}


	// no errorFnc
	static <API, T> API proxy(final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		callFnc.apply(proxy(api, resultFnc, finalFnc));
	}


	// just resultFnc
	static <API, T> API proxy(final API api, Util.Consumer<T> resultFnc) {
		return proxy(api, resultFnc, e -> LOG.error("errorFnc", e));
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		callFnc.apply(proxy(api, resultFnc));
	}
}
