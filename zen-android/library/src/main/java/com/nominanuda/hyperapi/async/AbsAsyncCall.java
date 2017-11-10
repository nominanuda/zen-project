package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;
import android.util.Pair;

import com.nominanuda.zen.common.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * Created by azum on 06.11.17.
 */

public abstract class AbsAsyncCall {

	interface ICancelable {
		void cancel();
	}

	protected final static Logger LOG = LoggerFactory.getLogger(AsyncCall.class);
	protected final static Runnable DEFAULT_FINAL_FNC = () -> {
		// do nothing
	};
	private final ICancelable mCancelable;


	protected AbsAsyncCall(ICancelable cancelable) {
		mCancelable = cancelable;
	}

	public void cancel() {
		mCancelable.cancel();
	}


	/* activity */

	// main
	static <API, T> Pair<API, ICancelable> proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		AsyncLoaderInvocationHandler<API, T> handler = new AsyncLoaderInvocationHandler<>(activity, activity.getLoaderManager(), api, resultFnc, errorFnc, finalFnc);
		return new Pair<>(apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), apiClass.getInterfaces(), handler)), handler::cancel);
	}

	// no finalFnc
	static <API, T> Pair<API, ICancelable> proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(activity, api, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no error Fnc
	static <API, T> Pair<API, ICancelable> proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(activity, api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	// just resultFnc
	static <API, T> Pair<API, ICancelable> proxy(final Activity activity, final API api, Util.Consumer<T> resultFnc) {
		return proxy(activity, api, resultFnc, DEFAULT_FINAL_FNC);
	}


	/* fragment */

	// main
	static <API, T> Pair<API, ICancelable> proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		AsyncLoaderInvocationHandler<API, T> handler = new AsyncLoaderInvocationHandler<>(fragment.getActivity(), fragment.getLoaderManager(), api, resultFnc, errorFnc, finalFnc);
		return new Pair<>(apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), apiClass.getInterfaces(), handler)), handler::cancel);
	}

	// no finalFnc
	static <API, T> Pair<API, ICancelable> proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(fragment, api, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	static <API, T> Pair<API, ICancelable> proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(fragment, api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	// just resultFnc
	static <API, T> Pair<API, ICancelable> proxy(final Fragment fragment, final API api, Util.Consumer<T> resultFnc) {
		return proxy(fragment, api, resultFnc, DEFAULT_FINAL_FNC);
	}


	/* suport fragment */

	// main
	static <API, T> Pair<API, ICancelable> proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		AsyncLoaderInvocationHandler<API, T> handler = new AsyncLoaderInvocationHandler<>(fragment.getActivity(), fragment.getActivity().getLoaderManager(), api, resultFnc, errorFnc, finalFnc);
		return new Pair<>(apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), apiClass.getInterfaces(), handler)), handler::cancel);
	}

	// no finalFnc
	static <API, T> Pair<API, ICancelable> proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(fragment, api, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	static <API, T> Pair<API, ICancelable> proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(fragment, api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	// just resultFnc
	static <API, T> Pair<API, ICancelable> proxy(final android.support.v4.app.Fragment fragment, final API api, Util.Consumer<T> resultFnc) {
		return proxy(fragment, api, resultFnc, DEFAULT_FINAL_FNC);
	}


	/* asnyc task */

	// main
	static <API, T> Pair<API, ICancelable> proxy(final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		AsyncTaskInvocationHandler<API, T> handler = new AsyncTaskInvocationHandler<>(api, resultFnc, errorFnc, finalFnc);
		return new Pair<>(apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), apiClass.getInterfaces(), handler)), handler::cancel);
	}

	// no finalFnc
	static <API, T> Pair<API, ICancelable> proxy(final API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		return proxy(api, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	static <API, T> Pair<API, ICancelable> proxy(final API api, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		return proxy(api, resultFnc, e -> LOG.error("errorFnc", e), finalFnc);
	}

	// just resultFnc
	static <API, T> Pair<API, ICancelable> proxy(final API api, Util.Consumer<T> resultFnc) {
		return proxy(api, resultFnc, DEFAULT_FINAL_FNC);
	}
}
