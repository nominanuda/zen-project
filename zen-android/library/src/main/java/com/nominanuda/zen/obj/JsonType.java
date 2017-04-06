/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.obj;

import com.nominanuda.zen.common.Check;

import org.json.JSONArray;
import org.json.JSONObject;

public enum JsonType {
	obj, arr, str, bool, num, nil;

	public static JsonType of(Object javaValue) {
		return
			javaValue == null ? nil :
			javaValue instanceof String ? str :
			javaValue instanceof Number ? num :
			javaValue instanceof Boolean ? bool :
			javaValue instanceof Arr ? arr :
			javaValue instanceof Obj ? obj :
			(JsonType)(Check.illegalargument.fail(javaValue.getClass() + " not a json type"))
		;
	}

	public static boolean isNullablePrimitive(Object javaValue) {
		return
			javaValue == null
			|| javaValue instanceof String
			|| javaValue instanceof Number
			|| javaValue instanceof Boolean;
	}


	public static boolean isNonNullPrimitiveVal(Object o) {
		return o != null && isNullablePrimitive(o);
	}

	public static boolean isJSONArray(Object val) {
		return val instanceof JSONArray;
	}

	public static boolean isJSONObject(Object val) {
		return val instanceof JSONObject;
	}
}
