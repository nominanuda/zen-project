package com.nominanuda.zen.hyperapi.async;

import android.support.v7.app.AppCompatActivity;

import com.nominanuda.zen.hyperapi.HyperApiFactory;

/**
 * Created by azum on 20/03/17.
 */

public abstract class HyperAppCompatActivity extends AppCompatActivity {
	protected HyperApiFactory hyperApiFactory;

	protected void load(Callback<?> cback) {
		getLoaderManager().initLoader(0, null, cback.getLoaderCallbacks(this));
	}
}
