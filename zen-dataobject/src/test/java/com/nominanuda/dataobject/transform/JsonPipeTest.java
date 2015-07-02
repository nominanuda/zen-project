/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.nominanuda.dataobject.transform;

import java.io.StringReader;

import org.junit.Test;
import org.junit.Assert;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructContentHandler;
import com.nominanuda.dataobject.JsonStreamingParser;
import com.nominanuda.lang.InstanceFactory;

public class JsonPipeTest {

	class AddOneTransformer extends BaseJsonTransformer {
		
		@Override
		public boolean primitive(Object value) throws RuntimeException {
			if(value instanceof Float) {
				Float n = ((Float) value).floatValue();
				return super.primitive(new Float(n + 1.0));
			}
			else if(value instanceof Double) {
				Double n = ((Double) value).doubleValue();
				return super.primitive(new Double(n + 1.0));
			}
			else if(value instanceof Integer) {
				Integer n = ((Integer) value).intValue();
				return super.primitive(new Integer(n + 1));
			}
			else if(value instanceof Long) {
				Long n = ((Long) value).longValue();
				return super.primitive(new Long(n + 1L));
			}
			else {
				return super.primitive(value);
			}
		}
	}


	@Test
	public void transformerOrderTest() {
		JsonPipeline p = new JsonPipeline()
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new StringValuesJsonTransformer()))
			.withLooseParser();

		DataStruct res = p.build(new StringReader("{a:{b:false,c:1.1,d:1.0,e:38}}")).apply();

		System.err.println(res.toString());
		Object v = ((DataStruct)(res.asObject().get("a"))).asObject().get("e");
		Assert.assertTrue(v instanceof String && ((String)v).equals("42"));
	}
	
	@Test
	public void rawPipelineTest() {
		JsonPipeline p = new JsonPipeline()
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new AddOneTransformer()))
			.add(new InstanceFactory<JsonTransformer>(new StringValuesJsonTransformer()));

		JsonTransformer t = p.build();
		DataStructContentHandler dsch = new DataStructContentHandler();
		t.setTarget(dsch);
		new JsonStreamingParser(true, new StringReader("{a:{b:false,c:1.1,d:1.0,e:38}}")).stream(t);

		System.err.println(dsch.getResult().toString());
		Object v = ((DataStruct)(dsch.getResult().asObject().get("a"))).asObject().get("e");
		Assert.assertTrue(v instanceof String && ((String)v).equals("42"));
	}

}
