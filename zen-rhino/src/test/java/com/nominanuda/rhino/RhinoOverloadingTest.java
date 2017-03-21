package com.nominanuda.rhino;

import java.util.Map;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;

public class RhinoOverloadingTest extends RhinoTestCase {
	@Override
	protected Map<String, Object> buildJavaObjectsMap() {
		Map<String, Object> m = super.buildJavaObjectsMap();
		
		m.put("overloads", new Object() {
			public String method(Number n) {
				return "number";
			}
			public String method(String s) {
				return "string";
			}
			public String method(DataStruct j) {
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
			public String method(DataArray a) {
				return "array";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("mapOverload", new Object() {
			public String method(DataObject a) {
				return "map";
			}
			public String method(Object o) {
				return "object";
			}
		});
		
		m.put("jsonOverload", new Object() {
			public String method(DataStruct j) {
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
				if (o instanceof DataArray) {
					return "array";
				}
				if (o instanceof DataObject) {
					return "map";
				}
				return "object";
			}
		});
		
		return m;
	}
}
