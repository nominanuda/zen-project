package com.nominanuda.zen.obj;

import com.nominanuda.zen.common.Check;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by azum on 17/03/17.
 */

public class Obj extends JSONObject implements Stru {
	public static Obj make(Object...keysAndVals) {
		Check.illegalargument.assertTrue(keysAndVals.length % 2 == 0, "odd number of arguments");
		Obj o = new Obj();
		if (keysAndVals != null) {
			int halflen = keysAndVals.length / 2;
			for (int i = 0; i < halflen; i++) {
				try {
					o.put((String) keysAndVals[i * 2], keysAndVals[i * 2 + 1]);
				} catch (JSONException e) {
					Check.illegalargument.fail();
				}
			}
		}
		return o;
	}

	public Obj() {
		super();
	}
	public Obj(String json) throws JSONException {
		super(json);
	}

	@Override
	public boolean isObj() {
		return true;
	}
	@Override
	public Obj asObj() throws ClassCastException {
		return this;
	}

	@Override
	public boolean isArr() {
		return false;
	}
	@Override
	public Arr asArr() throws ClassCastException {
		throw new ClassCastException();
	}
}
