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

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import com.nominanuda.lang.Check;

public class StringValuesJsonTransformer extends BaseJsonTransformer {
	
	@Override
	public boolean primitive(Object value) throws RuntimeException {
		if(value == null || value instanceof String) {
			return super.primitive(value);
		} else if(value instanceof Boolean) {
			return super.primitive(value.toString());
		} else {
			return super.primitive(STRUCT.numberToString(
					Check.illegalstate.assertInstanceOf(value, Number.class)));
		}
	}

}
