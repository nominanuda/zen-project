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

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.obj.wrap.ObjWrapper;


public class ScriptableConvertor {
	public static final ScriptableConvertor SCONVERTOR = new ScriptableConvertor();
	
	
	public Scriptable listToScriptable(Context cx, List<?> list, Scriptable topScope) {
		Scriptable res = RHINO.newArray(cx, topScope);
		final int len = list.size();
		for (int i = 0; i < len; i++) {
			RHINO.putProperty(res, i, convertObjectToRhino(cx, list.get(i), topScope));
		}
		return res;
	}
	
	public Scriptable mapToScriptable(Context cx, Map<?, ?> map, Scriptable topScope) {
		Scriptable res = RHINO.newObject(cx, topScope);
		for (Object k : map.keySet()) { // k has to be String or Number
			RHINO.putProperty(res, k, convertObjectToRhino(cx, map.get(k), topScope));
		}
		return res;
	}
	
	public Scriptable struToScriptable(Context cx, Stru stru, Scriptable topScope) {
		Scriptable res;
		if (stru.isArr()) {
			res = RHINO.newArray(cx, topScope);
			Arr a = (Arr)stru;
			final int len = a.len();
			for (int i = 0; i < len; i++) {
				Object val = a.get(i);
				if (JsonType.isNullablePrimitive(val)) {
					RHINO.putProperty(res, i, convertPrimitiveToRhino(val));
				} else {
					RHINO.putProperty(res, i, struToScriptable(cx, (Stru)val, topScope));
				}
			}
		} else {
			res = RHINO.newObject(cx, topScope);
			Obj o = (Obj)stru;
			for (String k : o.keySet()) {
				Object val = o.get(k);
				if (JsonType.isNullablePrimitive(val)) {
					RHINO.putProperty(res, k, convertPrimitiveToRhino(val));
				} else {
					RHINO.putProperty(res, k, struToScriptable(cx, (Stru)val, topScope));
				}
			}
		}
		return res;
	}
	
	private Object convertObjectToRhino(Context cx, Object val, Scriptable topScope) {
		if (JsonType.isNullablePrimitive(val)) {
			return convertPrimitiveToRhino(val);
		} else if (val instanceof List) {
			return listToScriptable(cx, (List<?>)val, topScope);
		} else if (val instanceof Map) {
			return mapToScriptable(cx, (Map<?, ?>)val, topScope);
		} else if (val instanceof ObjWrapper) {
			return struToScriptable(cx, (Stru)((ObjWrapper)val).unwrap(), topScope);
		} else if (val instanceof Stru) {
			return struToScriptable(cx, (Stru)val, topScope);
		}
		return val;
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
				Arr arr = ((Arr)res);
				arr.add(null); // prepare empty slot
				Object val = RHINO.getProperty(s, i);
				if (JsonType.isNullablePrimitive(val)) {
					arr.set(i, val);
				} else if (RHINO.isUndefined(val)) {
				} else {
					arr.set(i, fromScriptable((Scriptable)val));
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
