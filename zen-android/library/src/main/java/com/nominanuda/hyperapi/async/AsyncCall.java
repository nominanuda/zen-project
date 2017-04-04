package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.util.Pair;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncCall<API, T> {

	public static interface Function<API, T> {
		T apply(API api);
	}
	public static interface Callback<T> {
		void apply(T result);
	}

	public AsyncCall(final Activity activity, final API api, Function<API, T> callFnc, Callback<T> resultFnc, Callback<Exception> errorFnc) {
		Class<?> apiClass = api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
			apiClass.getClassLoader(), apiClass.getInterfaces(),
			new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
					activity.getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Pair<T, Exception>>() {
						@Override
						public Loader<Pair<T, Exception>> onCreateLoader(int id, Bundle args) {
							return new AsyncTaskLoader<Pair<T, Exception>>(activity) {
								private T mData = null;

								@Override
								protected void onStartLoading() {
									if (mData != null) {
										deliverResult(new Pair<T, Exception>(mData, null));
									} else {
										forceLoad();
									}
								}

								@Override
								public Pair<T, Exception> loadInBackground() {
									try {
										mData = (T) method.invoke(api, methodArgs);
										return new Pair<T, Exception>(mData, null);
									} catch (Exception e) {
										e.printStackTrace();
										return new Pair<T, Exception>(null, e);
									}
								}

								@Override
								public void deliverResult(Pair<T, Exception> result) {
									if (isStarted()) {
										super.deliverResult(result);
									}
								}
							};
						}

						@Override
						public void onLoadFinished(Loader<Pair<T, Exception>> loader, Pair<T, Exception> result) {
							if (result.second != null) {
								errorFnc.apply(result.second);
							} else {
								resultFnc.apply(result.first);
							}
						}

						@Override
						public void onLoaderReset(Loader<Pair<T, Exception>> loader) {

						}
					});
					return null;
				}
			}
		)));
	}

	public AsyncCall(final Activity activity, final API api, Function<API, T> callFnc, Callback<T> resultFnc) {
		this(activity, api, callFnc, resultFnc, e -> {
			// TODO default logging
		});
	}
}
