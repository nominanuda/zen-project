package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;

import com.nominanuda.zen.common.Util;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncVoidCall<API> extends AsyncCall<API, Boolean> {

	/* activity */

	// main
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		super(activity, api,
				apiProxy -> {
					callFnc.accept(apiProxy);
					return null;
				},
				result -> resultFnc.accept(true), // if AsyncCall called the resultFnc everything went ok
				errorFnc,
				finalFnc);
	}

	// no finalFnc
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(activity, api, callFnc, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Runnable finalFnc) {
		this(activity, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		}, finalFnc);
	}

	// just resultFnc
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(activity, api, callFnc, resultFnc, DEFAULT_FINAL_FNC);
	}

	// nothing
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc) {
		this(activity, api, callFnc, result -> LOG.info("resultFnc"));
	}


	/* fragment */

	// main
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		super(fragment, api,
				apiProxy -> {
					callFnc.accept(apiProxy);
					return null;
				},
				result -> resultFnc.accept(true), // if AsyncCall called the resultFnc everything went ok
				errorFnc,
				finalFnc);
	}

	// no finalFnc
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(fragment, api, callFnc, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Runnable finalFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		}, finalFnc);
	}

	// just resultFnc
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(fragment, api, callFnc, resultFnc, DEFAULT_FINAL_FNC);
	}

	// nothing
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc) {
		this(fragment, api, callFnc, result -> LOG.info("resultFnc"));
	}


	/* support fragment */

	// main
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		super(fragment, api,
				apiProxy -> {
					callFnc.accept(apiProxy);
					return null;
				},
				result -> resultFnc.accept(true), // if AsyncCall called the resultFnc everything went ok
				errorFnc,
				finalFnc);
	}

	// no finalFnc
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(fragment, api, callFnc, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Runnable finalFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		}, finalFnc);
	}

	// just resultFnc
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(fragment, api, callFnc, resultFnc, DEFAULT_FINAL_FNC);
	}

	// nothing
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc) {
		this(fragment, api, callFnc, result -> LOG.info("resultFnc"));
	}


	/* async task */

	// main
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		super(api,
				apiProxy -> {
					callFnc.accept(apiProxy);
					return null;
				},
				result -> resultFnc.accept(true), // if AsyncCall called the resultFnc everything went ok
				errorFnc,
				finalFnc);
	}

	// no finalFnc
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		this(api, callFnc, resultFnc, errorFnc, DEFAULT_FINAL_FNC);
	}

	// no errorFnc
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Runnable finalFnc) {
		this(api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		}, finalFnc);
	}

	// just resultFnc
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(api, callFnc, resultFnc, DEFAULT_FINAL_FNC);
	}

	// nothing
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc) {
		this(api, callFnc, result -> LOG.info("resultFnc"));
	}
}
