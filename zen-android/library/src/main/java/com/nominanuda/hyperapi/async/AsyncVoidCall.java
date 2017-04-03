package com.nominanuda.hyperapi.async;

import android.app.Activity;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncVoidCall<API> extends AsyncCall<API, Boolean> {
	public AsyncVoidCall(Activity activity, API api, Callback<API> callFnc, Callback<Boolean> resultFnc, Callback<Exception> errorFnc) {
		super(activity, api, apiProxy -> {
			callFnc.apply(apiProxy);
			return null;
		}, resultFnc, errorFnc);
	}
	public AsyncVoidCall(Activity activity, API api, Callback<API> callFnc, Callback<Boolean> resultFnc) {
		this(activity, api, callFnc, resultFnc, null);
	}
	public AsyncVoidCall(Activity activity, API api, Callback<API> callFnc) {
		this(activity, api, callFnc, null);
	}
}
