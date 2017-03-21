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
package com.nominanuda.rhino;

import static org.mozilla.javascript.RhinoHelper.RHINO;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;


public class StruScriptableConvertor {
	public static final StruScriptableConvertor DSS_CONVERTOR = new StruScriptableConvertor();
	
	public Scriptable toScriptable(Context cx, Stru source, Scriptable topScope) {
		Scriptable res;
		if (source.isArr()) {
			res = RHINO.newArray(cx, topScope);
			Arr a = (Arr)source;
			final int len = a.len();
			for (int i = 0; i < len; i++) {
				Object val = a.get(i);
				if (JsonType.isNullablePrimitive(val)) {
					RHINO.putProperty(res, i, convertPrimitiveToRhino(val));
				} else {
					RHINO.putProperty(res, i, toScriptable(cx, (Stru)val, topScope));
				}
			}
		} else {
			res = RHINO.newObject(cx, topScope);
			Obj o = (Obj)source;
			for (String k : o.keySet()) {
				Object val = o.get(k);
				if (JsonType.isNullablePrimitive(val)) {
					RHINO.putProperty(res, k, convertPrimitiveToRhino(val));
				} else {
					RHINO.putProperty(res, k, toScriptable(cx, (Stru)val, topScope));
				}
			}
		}
		return res;
	}

	private Object convertPrimitiveToRhino(Object val) {
		return val;//TODO date number
	}

	public Stru fromScriptable(Scriptable s) {
		Stru res;
		if (RHINO.isArray(s)) {
			res = Arr.make();
			NativeArray a = (NativeArray)s;//TODO unsafe
			final int len = (int)a.getLength();
			for (int i = 0; i < len; i++) {
				Object val = RHINO.getProperty(s, i);
				if (JsonType.isNullablePrimitive(val)) {
					((Arr)res).set(i, val);
				} else if (RHINO.isUndefined(val)) {
				} else {
					((Arr)res).set(i, fromScriptable((Scriptable)val));
				}
			}
		} else {
			res = Obj.make();
			for (Object k : s.getIds()) {
				Object val = RHINO.getProperty(s, k);
				if (JsonType.isNullablePrimitive(val)) {
					((Obj)res).put(k.toString(), val);
				} else if (RHINO.isUndefined(val)) {
				} else {
					((Obj)res).put(k.toString(), fromScriptable((Scriptable)val));
				}
			}
		}
		return res;
	}
}
