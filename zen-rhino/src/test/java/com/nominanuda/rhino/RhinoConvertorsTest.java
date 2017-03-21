package com.nominanuda.rhino;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.util.Map;

public class RhinoConvertorsTest extends RhinoTestCase {
	@Override
	protected Map<String, Object> buildJavaObjectsMap() {
		Map<String, Object> m = super.buildJavaObjectsMap();
		
		m.put("numberedMap", STRUCT.buildObject(
			"alpha", STRUCT.buildObject("key", "alpha"),
			"beta", STRUCT.buildObject("key", "beta"),
			"gamma", STRUCT.buildObject("key", "gamma"),
			"0", STRUCT.buildObject("key", "0"),
			"1", STRUCT.buildObject("key", "1"),
			"2", STRUCT.buildObject("key", "2"),
			"5", STRUCT.buildObject("key", "5")
		));
		
		m.put("normalArray", STRUCT.buildArray("one", "two", "three"));
		
		return m;
	}
}
