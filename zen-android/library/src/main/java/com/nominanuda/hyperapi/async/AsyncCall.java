package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

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

	public AsyncCall(final Activity activity, final API api, Function<API, T> callFnc, Callback<T> resultFnc, Callable<Exception> errorFnc) {
		Class<?> apiClass = api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
			apiClass.getClassLoader(), apiClass.getInterfaces(),
			new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
					activity.getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<T>() {
						@Override
						public Loader<T> onCreateLoader(int id, Bundle args) {
							return new AsyncTaskLoader<T>(activity) {
								private T mData = null;

								@Override
								protected void onStartLoading() {
									if (mData != null) {
										deliverResult(mData);
									} else {
										forceLoad();
									}
								}

								@Override
								public T loadInBackground() {
									try {
										return (T) method.invoke(api, methodArgs);
									} catch (Exception e) {
										e.printStackTrace();
										return null;
									}
								}

								@Override
								public void deliverResult(T data) {
									if (isStarted()) {
										super.deliverResult(data);
									}
								}
							};
						}

						@Override
						public void onLoadFinished(Loader<T> loader, T data) {
							resultFnc.apply(data);
						}

						@Override
						public void onLoaderReset(Loader<T> loader) {
						}
					});
					return null;
				}
			}
		)));
	}

	public AsyncCall(final Activity activity, final API api, Function<API, T> callFnc, Callback<T> resultFnc) {
		this(activity, api, callFnc, resultFnc, null);
	}
}
