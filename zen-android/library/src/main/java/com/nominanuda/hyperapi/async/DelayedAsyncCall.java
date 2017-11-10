package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;
import android.util.Pair;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by azum on 09.10.17.
 */

public class DelayedAsyncCall<API, PARAM, RESULT> extends AbsAsyncCall {
	private final AtomicBoolean mHasRun = new AtomicBoolean(false);
	private final Util.BiFunction<API, PARAM, RESULT> mCallBiFnc;
	private final API mApiProxy;


	private DelayedAsyncCall(Util.BiFunction<API, PARAM, RESULT> callBiFnc, Pair<API, ICancelable> proxy) {
		super(proxy.second);
		mCallBiFnc = callBiFnc;
		mApiProxy = proxy.first;
	}

	public void apply(PARAM param) {
		Check.illegalstate.assertTrue(mHasRun.compareAndSet(false, true), "already run");
		mCallBiFnc.apply(mApiProxy, param);
	}


	/* activity */

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(activity, api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callBiFnc, proxy(activity, api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(activity, api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final Activity activity, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc) {
		this(callBiFnc, proxy(activity, api, resultFnc));
	}


	/* fragment */

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc));
	}


	/* support fragment */

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc) {
		this(callBiFnc, proxy(fragment, api, resultFnc));
	}


	/* async task */

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(api, resultFnc, errorFnc, finalFnc));
	}

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(callBiFnc, proxy(api, resultFnc, errorFnc));
	}

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc, Runnable finalFnc) {
		this(callBiFnc, proxy(api, resultFnc, finalFnc));
	}

	public DelayedAsyncCall(final API api, Util.BiFunction<API, PARAM, RESULT> callBiFnc, Util.Consumer<RESULT> resultFnc) {
		this(callBiFnc, proxy(api, resultFnc));
	}
}
