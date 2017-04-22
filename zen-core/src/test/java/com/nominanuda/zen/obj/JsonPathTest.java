package com.nominanuda.zen.obj;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.seq.Seq;

public class JsonPathTest {
	
	@Test
	public void testSetOrPushPathProperty() {
		List<Tuple2<String, Object>> entries = Seq.SEQ.buildList(ArrayList.class,
				new Tuple2<String, Object>("data.steps", "disabled"),
				new Tuple2<String, Object>("data.topics", "dibop-artistfirst-xml"),
				new Tuple2<String, Object>("data.topics", "dibop-believe-xml"),
				new Tuple2<String, Object>("data.topics", "musicload-tunecore-xml"),
				new Tuple2<String, Object>("types.topics", "checks"),
				new Tuple2<String, Object>("data.doStart", "Start"),
				new Tuple2<String, Object>("isAjax", true),
				new Tuple2<String, Object>("snipId", "dashboard")
		);
		
		Obj result = Obj.make();
		for (Tuple2<String, Object> e : entries) {
			JsonPath.JPATH.setOrPushPathProperty(result, e.get0(), e.get1());
		}
		
		System.out.println(result.toString());
	}
}
