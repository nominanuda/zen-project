package com.nominanuda.hyperapi.async;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.zen.common.Util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Created by azum on 13/05/17.
 */

public class AsyncLoaderInvocationHandler<API, T> implements InvocationHandler {

	private static class AsyncCallLoader<API, RESULT> extends AsyncTaskLoader<Pair<RESULT, Exception>> {
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
		public Pair<RESULT, Exception> loadInBackground() {
			try {
				RESULT data = (RESULT) mMethod.invoke(mApi, mMethodArgs);
				return new Pair<>(data, null);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				@Nullable Throwable cause = e.getCause();
				return new Pair<>(null, cause instanceof UndeclaredThrowableException
						? (Exception) cause.getCause()
						: cause instanceof HttpAppException
						? (HttpAppException) cause
						: cause instanceof Exception
						? new Http500Exception((Exception) cause)
						: new Http500Exception(e));
			} catch (Exception e) {
				e.printStackTrace();
				return new Pair<>(null, e);
			}
		}

		@Override
		public void deliverResult(Pair<RESULT, Exception> result) {
			if (isStarted()) {
				super.deliverResult(result);
			}
		}
	}


	private final Context mCtx;
	private final LoaderManager mLoaderManager;
	private final API mApi;
	private final Util.Consumer<T> mResultFnc;
	private final Util.Consumer<Exception> mErrorFnc;
	private final Runnable mFinalFnc;
	private int mLoaderId = -1;

	AsyncLoaderInvocationHandler(Context ctx, LoaderManager loaderManager, API api,
								 Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc, Runnable finalFnc) {
		mCtx = ctx;
		mLoaderManager = loaderManager;
		mApi = api;
		mResultFnc = resultFnc;
		mErrorFnc = errorFnc;
		mFinalFnc = finalFnc;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
		mLoaderId = (int) (Math.random() * 10000); // TODO uniqueness
		mLoaderManager.initLoader(mLoaderId, null, new LoaderManager.LoaderCallbacks<Pair<T, Exception>>() {
			@Override
			public Loader<Pair<T, Exception>> onCreateLoader(int id, Bundle args) {
				return new AsyncCallLoader<>(mCtx, mApi, method, methodArgs);
			}

			@Override
			public void onLoadFinished(Loader<Pair<T, Exception>> loader, Pair<T, Exception> result) {
				if (result.second != null) {
					mErrorFnc.accept(result.second);
				} else {
					mResultFnc.accept(result.first);
				}
				mFinalFnc.run();
				mLoaderManager.destroyLoader(mLoaderId);
			}

			@Override
			public void onLoaderReset(Loader<Pair<T, Exception>> loader) {

			}
		});
		return null; // just for signature check
	}

	void cancel() {
		if (mLoaderId > -1) {
			mLoaderManager.destroyLoader(mLoaderId);
			// TODO call finalFnc?
		}
	}
}
