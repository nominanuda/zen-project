package com.nominanuda.dataobject.schema;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.transform.JsonPipeline;
import com.nominanuda.dataobject.transform.JsonTransformer;
import com.nominanuda.dataobject.transform.StringValuesJsonTransformer;
import com.nominanuda.lang.InstanceFactory;

public class JclValidatorTest {

	//@Test
	public void test() throws Exception {
		JclValidator jclValidator = new JclValidator("{a:{r,c,e}}");
		DataObjectImpl in = new DataObjectImpl();
		in.put("a", 1);
		JsonPipeline p = new JsonPipeline()
			.add(new InstanceFactory<JsonTransformer>(jclValidator))
			.withLooseParser()
			.complete();
		DataStruct res = p.build(new StringReader("{a:{b:false,c:1.1,d:1.0,e:[1,[1],{x:1}]}}")).apply();
		System.err.println(res.toString());
	}

	@Test
	public void test1() throws Exception {
		JclValidatorFactory f = new JclValidatorFactory("{a:{r:b,c!:n,e:s}}");
		JsonPipeline p = new JsonPipeline()
			.add(f)
			.withLooseParser()
			.complete();
		DataStruct res = p.build(new StringReader("{a:{r:false,c:1.1,e:''}}")).apply();
		System.err.println(res.toString());
	}

}
