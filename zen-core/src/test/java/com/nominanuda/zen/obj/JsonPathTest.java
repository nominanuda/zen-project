package com.nominanuda.zen.obj;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.seq.Seq;

@Ignore
public class JsonPathTest {
	
	@Test
	public void testSetOrPushPathProperty() {
		List<Tuple2<String, Object>> entries = Seq.SEQ.buildList(ArrayList.class,
				new Tuple2<String, Object>("data.topics.6", "dixero-warner-xml"),
				new Tuple2<String, Object>("data.topics.7", "musicload-believe-xml"),
				new Tuple2<String, Object>("data.topics.8", "musicload-daredo-xml"),
				new Tuple2<String, Object>("data.topics.9", "musicload-goodtogo-xml"),
				new Tuple2<String, Object>("data.doStart", "Start"),
				new Tuple2<String, Object>("data.steps", "disabled"),
				new Tuple2<String, Object>("data.topics.10", "musicload-kontor-xml"),
				new Tuple2<String, Object>("data.topics.11", "musicload-tunecore-xml"),
				new Tuple2<String, Object>("data.topics.12", "timmusic-artistfirst-xml"),
				new Tuple2<String, Object>("data.topics.0", "dibop-artistfirst-xml"),
				new Tuple2<String, Object>("data.topics.1", "dibop-believe-xml"),
				new Tuple2<String, Object>("data.topics.2", "dibop-emi-xml"),
				new Tuple2<String, Object>("data.topics.13", "timmusic-kiver-xml"),
				new Tuple2<String, Object>("data.topics.3", "dibop-fuga-xml"),
				new Tuple2<String, Object>("data.topics.14", "timmusic-theorchard-xml"),
				new Tuple2<String, Object>("data.topics.4", "dibop-theorchard-xml"),
				new Tuple2<String, Object>("data.topics.5", "dibop-universal-xml"),
				new Tuple2<String, Object>("types.topics", "checks")
		);
		
		Obj result = Obj.make();
		for (Tuple2<String, Object> e : entries) {
			JsonPath.JPATH.setOrPushPathProperty(result, e.get0(), e.get1());
		}
		
		System.out.println(result.toString());
	}
}
