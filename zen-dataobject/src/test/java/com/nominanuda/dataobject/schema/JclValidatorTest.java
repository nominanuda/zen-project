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
 */
package com.nominanuda.dataobject.schema;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Ignore;
import org.junit.Test;

import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.transform.JsonPipeline;
import com.nominanuda.dataobject.transform.JsonTransformer;
import com.nominanuda.dataobject.transform.StringValuesJsonTransformer;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.InstanceFactory;

//@Ignore
public class JclValidatorTest {


	@Test
	public void testValid() throws Exception {
//		valid("{}", "{}");
//		invalid("{}", "{a:null}");
//		valid("{a}", "{a:1}");
//		valid("{a?}", "{}");
//		invalid("{a}", "{}");
//		valid("{c,a?,b}", "{b:1,c:2}");
//		valid("{a!}", "{a:1}");
//		invalid("{a!}", "{a:null}");
//		valid("{a?:{}}", "{a:null}");
//		invalid("{a!:{}}", "{a:null}");
//		valid("{a!:{}}", "{a:{}}");
//		invalid("{a!:{}}", "{a:2}");
//		valid("{a,*}", "{b:{},a:1}");
//		valid("{a,b:{*}}", "{b:{c:{d:1}},a:1}");
//		valid("[]", "[]");
//		valid("[b]", "[true]");
//		invalid("[]", "[true]");
//		invalid("[b,b]", "[true]");
//		invalid("[b,b]", "[true,null,null]");
//		valid("[b,b,*]", "[true,true,true,1]");
		valid("MyType@[b] MyType2@[b]", "[true]");
	}

	private void invalid(String schema, String instance) throws Exception {
		try {
			valid(schema, instance);
			fail();
		} catch(RuntimeException e) {
			if(! (e.getCause() != null 
					&& e.getCause() instanceof ValidationException)) {
				throw e;
			}
		}
	}

	private void valid(String schema, String instance) throws Exception {
		DataStruct res = DataStructHelper.STRUCT.parse(instance, true);
		JclValidatorFactory f = new JclValidatorFactory(schema);
		JsonPipeline p = new JsonPipeline()
			.add(f)
			.withLooseParser()
			.complete();
		DataStruct res2 = p.build(res).apply();
		assertEquals(res, res2);
	}
}
