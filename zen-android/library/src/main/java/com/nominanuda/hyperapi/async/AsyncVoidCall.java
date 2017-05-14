package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;

import com.nominanuda.zen.common.Util;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncVoidCall<API> extends AsyncCall<API, Boolean> {

	/* activity */

	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		super(activity, api, apiProxy -> {
			callFnc.accept(apiProxy);
			return null;
		}, result -> {
			resultFnc.accept(true); // if AsyncCall called the resultFnc everything went ok
		}, errorFnc);
	}
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(activity, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		});
	}
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc) {
		this(activity, api, callFnc, result -> {
			LOG.info("resultFnc");
		});
	}


	/* fragment */

	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		super(fragment, api, apiProxy -> {
			callFnc.accept(apiProxy);
			return null;
		}, result -> {
			resultFnc.accept(true); // if AsyncCall called the resultFnc everything went ok
		}, errorFnc);
	}
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		});
	}
	public AsyncVoidCall(Fragment fragment, API api, Util.Consumer<API> callFnc) {
		this(fragment, api, callFnc, result -> {
			LOG.info("resultFnc");
		});
	}


	/* support fragment */

	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		super(fragment, api, apiProxy -> {
			callFnc.accept(apiProxy);
			return null;
		}, result -> {
			resultFnc.accept(true); // if AsyncCall called the resultFnc everything went ok
		}, errorFnc);
	}
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		});
	}
	public AsyncVoidCall(android.support.v4.app.Fragment fragment, API api, Util.Consumer<API> callFnc) {
		this(fragment, api, callFnc, result -> {
			LOG.info("resultFnc");
		});
	}


	/* async task */

	public AsyncVoidCall(API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		super(api, apiProxy -> {
			callFnc.accept(apiProxy);
			return null;
		}, result -> {
			resultFnc.accept(true); // if AsyncCall called the resultFnc everything went ok
		}, errorFnc);
	}
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(api, callFnc, resultFnc, e -> {
			LOG.error("errorFnc", e);
			resultFnc.accept(false); // if AsyncCall called the errorFnc something went wrong
		});
	}
	public AsyncVoidCall(API api, Util.Consumer<API> callFnc) {
		this(api, callFnc, result -> {
			LOG.info("resultFnc");
		});
	}
}
