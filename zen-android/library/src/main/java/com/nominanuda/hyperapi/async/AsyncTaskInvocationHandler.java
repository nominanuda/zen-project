package com.nominanuda.hyperapi.async;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.zen.common.Util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by azum on 13/05/17.
 */

public class AsyncTaskInvocationHandler<API, T> implements InvocationHandler {

	private static class AsyncCallTask<API, RESULT> extends AsyncTask<Object, Void, Pair<RESULT, Exception>> {
		private final API mApi;
		private final Method mMethod;
		private final Util.Consumer<RESULT> mResultFnc;
		private final Util.Consumer<Exception> mErrorFnc;

		private AsyncCallTask(API api, Method method, Util.Consumer<RESULT> resultFnc, Util.Consumer<Exception> errorFnc) {
			mApi = api;
			mMethod = method;
			mResultFnc = resultFnc;
			mErrorFnc = errorFnc;
		}

		@Override
		protected Pair<RESULT, Exception> doInBackground(Object... args) {
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			try {
				RESULT data = (RESULT) mMethod.invoke(mApi, args);
				return new Pair<RESULT, Exception>(data, null);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				Throwable cause = e.getCause();
				return new Pair<RESULT, Exception>(null, (cause != null && cause instanceof HttpAppException)
						? (HttpAppException) cause
						: new Http500Exception(e)
				);
			} catch (Exception e) {
				e.printStackTrace();
				return new Pair<RESULT, Exception>(null, e);
			}
		}

		@Override
		protected void onPostExecute(Pair<RESULT, Exception> result) {
			if (result.second != null) {
				mErrorFnc.accept(result.second);
			} else {
				mResultFnc.accept(result.first);
			}
		}
	}


	private final API mApi;
	private final Util.Consumer<T> mResultFnc;
	private final Util.Consumer<Exception> mErrorFnc;

	AsyncTaskInvocationHandler(API api, Util.Consumer<T> resultFnc, Util.Consumer<Exception> errorFnc) {
		mApi = api;
		mResultFnc = resultFnc;
		mErrorFnc = errorFnc;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		AsyncCallTask task = new AsyncCallTask<API, T>(mApi, method, mResultFnc, mErrorFnc);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			task.execute(args);
		} else {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
		}
		return null; // just for signature check
	}
}
