package com.nominanuda.hyperapi.async;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Pair;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.zen.common.Util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by azum on 27/03/17.
 */

public class AsyncCall<API, T> {

	private static class AsyncCallLoader<API, T> extends AsyncTaskLoader<Pair<T, Exception>> {
		private final API mApi;
		private final Method mMethod;
		private final Object[] mMethodArgs;

		private AsyncCallLoader(Context ctx, API api, Method method, Object[] methodArgs) {
			super(ctx);
			mApi = api;
			mMethod = method;
			mMethodArgs = methodArgs;
//			new ForceLoadContentObserver(); TODO?
		}

		@Override
		protected void onStartLoading() {
			forceLoad();
		}

		@Override
		protected void onStopLoading() {
			cancelLoad();
		}

		@Override
		protected void onForceLoad() {
			super.onForceLoad();
		}

		@Override
		protected void onReset() {
			onStopLoading();
		}

		@Override
		public Pair<T, Exception> loadInBackground() {
			try {
				T data = (T) mMethod.invoke(mApi, mMethodArgs);
				return new Pair<T, Exception>(data, null);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				return new Pair<T, Exception>(null, (cause != null && cause instanceof HttpAppException)
						? (HttpAppException) cause
						: new Http500Exception(e)
				);
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
	}


	private static class AsyncCallInvocationHandler<API, T> implements InvocationHandler {
		private final Context mCtx;
		private final LoaderManager mLoaderManager;
		private final API mApi;
		private final Util.Consumer<T> mResultFnc;
		private final Util.Consumer<Exception> mErrorFnc;

		private AsyncCallInvocationHandler(Context ctx, LoaderManager loaderManager, API api,
										   Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
			mCtx = ctx;
			mLoaderManager = loaderManager;
			mApi = api;
			mResultFnc = resultFnc;
			mErrorFnc = errorFnc;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
			final int loaderId = 0; // TODO uniqueness
			mLoaderManager.initLoader(loaderId, null, new LoaderManager.LoaderCallbacks<Pair<T, Exception>>() {
				@Override
				public Loader<Pair<T, Exception>> onCreateLoader(int id, Bundle args) {
					return new AsyncCallLoader<API, T>(mCtx, mApi, method, methodArgs);
				}

				@Override
				public void onLoadFinished(Loader<Pair<T, Exception>> loader, Pair<T, Exception> result) {
					if (result.second != null) {
						mErrorFnc.accept(result.second);
					} else {
						mResultFnc.accept(result.first);
					}
					mLoaderManager.destroyLoader(loaderId);
				}

				@Override
				public void onLoaderReset(Loader<Pair<T, Exception>> loader) {

				}
			});
			return null; // just for signature check
		}
	}


	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncCallInvocationHandler<API, T>(activity, activity.getLoaderManager(), api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final Activity activity, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(activity, api, callFnc, resultFnc, e -> {
			// TODO default logging
		});
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncCallInvocationHandler<API, T>(fragment.getActivity(), fragment.getLoaderManager(), api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			// TODO default logging
		});
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		Class<API> apiClass = (Class<API>) api.getClass();
		callFnc.apply((API) apiClass.cast(Proxy.newProxyInstance(
				apiClass.getClassLoader(), apiClass.getInterfaces(),
				new AsyncCallInvocationHandler<API, T>(fragment.getActivity(), fragment.getActivity().getLoaderManager(), api, resultFnc, errorFnc)
		)));
	}

	public AsyncCall(final android.support.v4.app.Fragment fragment, final API api, Util.Function<API, T> callFnc, Util.Consumer<T> resultFnc) {
		this(fragment, api, callFnc, resultFnc, e -> {
			// TODO default logging
		});
	}
}
