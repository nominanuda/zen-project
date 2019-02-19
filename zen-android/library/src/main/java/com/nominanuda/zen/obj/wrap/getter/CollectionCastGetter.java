package com.nominanuda.zen.obj.wrap.getter;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;


/**
 * Always returns a collection, even if the value inside the source json is not an Arr.
 * In particular:
 * <br>- if the value is not null (and not an Arr), it will be used as the first/only element of the returned collection.
 * <br>- if the value is null, the returned collection will be empty.
 */
@ThreadSafe
public class CollectionCastGetter extends CollectionGetter {
	public final static CollectionCastGetter GETTER = new CollectionCastGetter();
	
	protected CollectionCastGetter() {
		// just to avoid constructors during usage from outside
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Object extract(JSONObject o, Method getter) throws Throwable {
		final Class<?> type = getter.getReturnType();
		Object ob = o.get(getter.getName());
		if (type.equals(Arr.class)) {
			Arr arr = Arr.make();
			if (ob != null) {
				arr.put(fromObjValue(ob, ob.getClass()));
			}
			return arr;
		}
		Collection<Object> coll = type.isInterface() ? new LinkedList<>() : (Collection<Object>) type.newInstance();
		@Nullable Class<?> itemType = null;
		try {
			itemType = getCollectionReturnComponentType(getter);
		} catch (Exception e) {
			// dynamic mode on
		}
		if (JsonType.isJSONArray(ob)) {
			JSONArray arr = (JSONArray) ob;
			for (int i = 0, l = arr.length(); i < l; i++) {
				Object val = arr.isNull(i) ? null : arr.opt(i); // as per issue https://issuetracker.google.com/issues/36924550
				coll.add(fromObjValue(val, itemType));
			}
		} else if (ob != null) {
			coll.add(fromObjValue(ob, itemType));
		}
		return coll;
	}
}
