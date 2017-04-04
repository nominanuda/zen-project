package com.nominanuda.hyperapi.async;

import android.app.Activity;

import com.nominanuda.zen.common.Util;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncVoidCall<API> extends AsyncCall<API, Boolean> {
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc, Util.Consumer<Exception> errorFnc) {
		super(activity, api, apiProxy -> {
			callFnc.accept(apiProxy);
			return null;
		}, resultFnc, errorFnc);
	}
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc, Util.Consumer<Boolean> resultFnc) {
		this(activity, api, callFnc, resultFnc, null);
	}
	public AsyncVoidCall(Activity activity, API api, Util.Consumer<API> callFnc) {
		this(activity, api, callFnc, null);
	}
}
