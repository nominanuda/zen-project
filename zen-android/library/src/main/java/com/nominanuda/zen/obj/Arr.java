package com.nominanuda.zen.obj;

import com.nominanuda.zen.common.Check;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by azum on 22/03/17.
 */

public class Arr extends JSONArray implements Stru {
	public static Arr make(Object...vals) {
		Arr arr = new Arr();
		if (vals != null) {
			for (int i = 0; i < vals.length; i++) {
				arr.put(vals[i]);
			}
		}
		return arr;
	}
	public static Arr make(JSONArray json) {
		try {
			return new Arr(json);
		} catch (JSONException e) {
			Check.illegalargument.fail();
		}
		return null;
	}

	public Arr() {
		super();
	}
	public Arr(String json) throws JSONException {
		super(json);
	}
	public Arr(JSONArray json) throws JSONException {
		throw new UnsupportedOperationException("TODO");
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
