package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Util;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.nominanuda.hyperapi.async.AsyncCall.proxy;

/**
 * Created by azum on 09.10.17.
 */

public class DelayedAsyncCall<API, PARAM, RESULT> {
	private final AtomicBoolean mHasRun = new AtomicBoolean(false);
	private final Util.BiFunction<API, PARAM, RESULT> mCallFnc;
	private final API mProxy;


	private DelayedAsyncCall(Util.BiFunction<API, PARAM, RESULT> callFnc, API proxy) {
		mCallFnc = callFnc;
		mProxy = proxy;
	}

	public void apply(PARAM param) {
		Check.illegalstate.assertTrue(mHasRun.compareAndSet(false, true), "already run");
		mCallFnc.apply(mProxy, param);
	}


	/* activity */

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(activity, api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(activity, api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(activity, api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc) {
		this(callFnc, proxy(activity, api, resultFnc));
	}


	/* fragment */

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc) {
		this(callFnc, proxy(fragment, api, resultFnc));
	}


	/* support fragment */

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(fragment, api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc) {
		this(callFnc, proxy(fragment, api, resultFnc));
	}


	/* async task */

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callFnc, proxy(api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callFnc, proxy(api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callFnc, proxy(api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callFnc, Util.Consumer<RESULT> resultFnc) {
		this(callFnc, proxy(api, resultFnc));
	}
}
