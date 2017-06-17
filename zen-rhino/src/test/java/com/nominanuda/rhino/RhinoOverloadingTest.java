package com.nominanuda.rhino;

import java.util.Map;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class RhinoOverloadingTest extends RhinoTestCase {
	@Override
	@SuppressWarnings("unused")
	protected Map<String, Object> buildJavaObjectsMap() {
		Map<String, Object> m = super.buildJavaObjectsMap();
		
		m.put("overloads", new Object() {
			public String method(Number n) {
				return "number";
			}
			public String method(String s) {
				return "string";
			}
			public String method(Stru j) {
				return "json";
			}
		});
		
		m.put("numberOverload", new Object() {
			public String method(Number n) {
				return "number";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("stringOverload", new Object() {
			public String method(String s) {
				return "string";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("arrayOverload", new Object() {
			public String method(Arr a) {
				return "array";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("mapOverload", new Object() {
			public String method(Obj a) {
				return "map";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("jsonOverload", new Object() {
			public String method(Stru j) {
				return "json";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("noOverloads", new Object() {
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("instanceOf", new Object() {
			public String method(Object o) {
				if(o instanceof Arr) {
					return "array";
				}
				if(o instanceof Obj) {
					return "map";
				}
				return "object";
			}
		});
		
		return m;
	}
}
