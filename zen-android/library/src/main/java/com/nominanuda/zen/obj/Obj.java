package com.nominanuda.zen.obj;

import com.nominanuda.zen.common.Check;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by azum on 17/03/17.
 */

public class Obj extends JSONObject implements Stru {
	public static Obj make(Object...keysAndVals) {
		Obj o = new Obj();
		if (keysAndVals != null) {
			Check.illegalargument.assertTrue(keysAndVals.length % 2 == 0, "odd number of arguments");
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
	public static Obj make(JSONObject json) {
		try {
			return new Obj(json);
		} catch (JSONException e) {
			Check.illegalargument.fail();
		}
		return null;
	}
	public static Obj make(String json) {
		try {
			return new Obj(json);
		} catch (JSONException e) {
			Check.illegalargument.fail();
		}
		return null;
	}

	public Obj() {
		super();
	}
	public Obj(String json) throws JSONException {
		super(json);
	}
	private Obj(JSONObject json) throws JSONException {
		super(json, names(json));
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

	private static String[] names(JSONObject json) {
		Iterator<String> i = json.keys();
		ArrayList<String> n = new ArrayList<>();
		while (i.hasNext()) {
			n.add(i.next());
		}
		return n.toArray(new String[n.size()]);
	}
}
