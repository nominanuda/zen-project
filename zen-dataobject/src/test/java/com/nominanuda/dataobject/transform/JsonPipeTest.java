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

import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.InstanceFactory;

public class JsonPipeTest {

	@Test
	public void test() {
		DataObjectImpl in = new DataObjectImpl();
		in.put("a", 1);
		JsonPipeline p = new JsonPipeline()
			.add(new InstanceFactory<JsonTransformer>(new StringValuesJsonTransformer()))
			.withLooseParser()
			.complete();
		DataStruct res = p.build(new StringReader("{a:{b:false,c:1.1,d:1.0}}")).apply();
		System.err.println(res.toString());
	}

}
