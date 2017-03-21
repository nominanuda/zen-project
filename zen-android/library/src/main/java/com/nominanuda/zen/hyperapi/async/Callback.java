package com.nominanuda.zen.hyperapi.async;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

/**
 * Created by azum on 20/03/17.
 */

public abstract class Callback<T> {
	private final Object object;

	public Callback(T object) {
		this.object = object;
	}

	protected abstract void apply(T data);

	LoaderManager.LoaderCallbacks<T> getLoaderCallbacks(final Context ctx) {
		return new LoaderManager.LoaderCallbacks<T>() {
			@Override
			public Loader<T> onCreateLoader(int id, Bundle args) {
				return new Loader<>(ctx);
			}

			@Override
			public void onLoadFinished(Loader<T> loader, T data) {
				apply(data);
			}

			@Override
			public void onLoaderReset(Loader<T> loader) {
			}
		};
	}
}
