package com.nominanuda.zen.obj;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

class StruUtils {

	//private
	public static void deepCopy(Map<?, ?> m, Obj o) {
		for (Entry<?, ?> e : m.entrySet()) {
			String key = (String)e.getKey();
			Object v = e.getValue();
			if (v == null) {
				o.put(key, null);
			} else if (v instanceof Map<?, ?>) {
				Obj tobj = o.putObj(key);
				deepCopy((Map<?,?>)v, tobj);
			} else if (v instanceof Collection<?>) {
				Arr tarr = o.putArr(key);
				StruUtils.deepCopy((Collection<?>)v, tarr);
			} else {
				o.put(key, v);
			}
		}
	}

	//private
	public static void deepCopy(Collection<?> c, Arr tarr) {
		for (Object v : c) {
			if (v == null) {
				tarr.add(null);
			} else if (v instanceof Map<?, ?>) {
				Obj tobj = tarr.addObj();
				deepCopy((Map<?, ?>) v, tobj);
			} else if (v instanceof Collection<?>) {
				Arr a = tarr.addArr();
				deepCopy((Collection<?>) v, a);
			} else {
				tarr.add(v);
			}
		}
	}

}
