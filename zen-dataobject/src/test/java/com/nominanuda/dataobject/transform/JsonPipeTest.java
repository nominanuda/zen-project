package com.nominanuda.dataobject.transform;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.xml.SAXPipeline;

public class JsonPipeTest {

	@Test
	public void test() {
		DataObjectImpl in = new DataObjectImpl();
		in.put("a", 1);
		JsonPipeline p = new JsonPipeline()
			.add(new InstanceFactory<JsonTransformer>(new StringValuesJsonTransformer()))
			.withLooseParser()
			.complete();
		DataStruct res = p.build(in).apply();
		System.err.println(res.toString());
	}

}
