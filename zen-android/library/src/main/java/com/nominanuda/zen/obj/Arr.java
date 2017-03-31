package com.nominanuda.zen.obj;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by azum on 22/03/17.
 */

public class Arr extends JSONArray implements Stru {
	public Arr() {
		super();
	}
	public Arr(String json) throws JSONException {
		super(json);
	}

	@Override
	public boolean isObj() {
		return false;
	}

	@Override
	public Obj asObj() throws ClassCastException {
		throw new ClassCastException();
	}

	@Override
	public boolean isArr() {
		return true;
	}

	@Override
	public Arr asArr() throws ClassCastException {
		return this;
	}
}
