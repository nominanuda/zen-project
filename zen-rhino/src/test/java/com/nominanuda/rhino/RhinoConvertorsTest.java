package com.nominanuda.rhino;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;
import com.nominanuda.zen.obj.wrap.Wrap;
import com.nominanuda.zen.seq.Seq;

public class RhinoConvertorsTest extends RhinoTestCase {
	
	interface MyWrapper extends ObjWrapper {
		MyWrapper string(String s);
	}
	
	
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
		
		
		m.put("emptyList", new ArrayList<>());
		m.put("scalarList", Seq.SEQ.buildList(ArrayList.class,
			"a", "b", "c"));
		m.put("wrapperList", Seq.SEQ.buildList(ArrayList.class,
			Wrap.WF.wrap(MyWrapper.class).string("a"),
			Wrap.WF.wrap(MyWrapper.class).string("b"),
			Wrap.WF.wrap(MyWrapper.class).string("c")));

		m.put("emptyMap", new HashMap<>());
		m.put("scalarMap", Seq.SEQ.buildMap(HashMap.class,
			"a", "A",
			"b", "B",
			"c", "C"));
		m.put("wrapperMap", Seq.SEQ.buildMap(HashMap.class,
			"a", Wrap.WF.wrap(MyWrapper.class).string("A"),
			"b", Wrap.WF.wrap(MyWrapper.class).string("B"),
			"c", Wrap.WF.wrap(MyWrapper.class).string("C")));
		
		return m;
	}
}
