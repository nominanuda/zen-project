package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;
import android.util.Pair;

import com.nominanuda.zen.common.Util;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncCall<API, T> extends AbsAsyncCall {

	private AsyncCall(Util.Function<API, T> callFnc, Pair<API, ICancelable> proxy) {
		super(proxy.second);
		callFnc.apply(proxy.first);
	}


	/* activity */

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(activity, api, resultFnc, errorFnc, finalFnc));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(activity, api, resultFnc, errorFnc));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(activity, api, resultFnc, finalFnc));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(callFnc, proxy(activity, api, resultFnc));
	}



	/* fragment */

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, finalFnc));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(callFnc, proxy(fragment, api, resultFnc));
	}



	/* support fragment */

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, finalFnc));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(callFnc, proxy(fragment, api, resultFnc));
	}



	/* async task */

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(api, resultFnc, errorFnc, finalFnc));
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(api, resultFnc, errorFnc));
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(api, resultFnc, finalFnc));
	}

	public AsyncCall(final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(callFnc, proxy(api, resultFnc));
	}
}
