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

import org.junit.Test;

import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.transform.JsonPipeline;
import com.nominanuda.dataobject.transform.JsonTransformer;
import com.nominanuda.dataobject.transform.StringValuesJsonTransformer;
import com.nominanuda.lang.InstanceFactory;

public class JclValidatorTest {


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
