package com.nominanuda.rhino;

import java.util.Map;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;

public class RhinoConvertorsTest extends RhinoTestCase {
	@Override
	protected Map<String, Object> buildJavaObjectsMap() {
		Map<String, Object> m = super.buildJavaObjectsMap();
		
		m.put("numberedMap", Obj.make(
			"alpha", Obj.make("key", "alpha"),
			"beta", Obj.make("key", "beta"),
			"gamma", Obj.make("key", "gamma"),
			"0", Obj.make("key", "0"),
			"1", Obj.make("key", "1"),
			"2", Obj.make("key", "2"),
			"5", Obj.make("key", "5")
		));
		
		m.put("normalArray", Arr.make("one", "two", "three"));
		
		return m;
	}
}
