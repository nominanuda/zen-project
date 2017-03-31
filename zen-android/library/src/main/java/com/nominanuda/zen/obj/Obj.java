package com.nominanuda.zen.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by azum on 17/03/17.
 */

public class Obj extends JSONObject implements Stru {
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

	public Obj getObj(String k) {
		JSONObject o = optJSONObject(k);
		try {
			return o != null ? new Obj(o) : null;
		} catch (JSONException e) {
			return null;
		}
	}
	public Arr getArr(String k) {
		Arr arr = null;
		JSONArray a = optJSONArray(k);
		if (a != null) {
			arr = new Arr();
			for (int i=0, l=a.length(); i < l; i++) {
				arr.put(a.opt(i));
			}

		}
		return arr;
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
