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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataArrayImpl;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;

public class DataStructScriptableConvertor {
	private static final DataStructHelper structHelper = new DataStructHelper();
	private static final RhinoHelper rhino = new RhinoHelper();

	public Scriptable toScriptable(Context cx, DataStruct<?> source, Scriptable topScope) {
		Scriptable res;
		if(source.isArray()) {
			res = rhino.newArray(cx, topScope);
			DataArray a = (DataArray)source;
			final int len = a.getLength();
			for(int i = 0; i < len; i++) {
				Object val = a.get(i);
				if(structHelper.isPrimitiveOrNull(val)) {
					rhino.putProperty(res, i, convertPrimitiveToRhino(val));
				} else {
					rhino.putProperty(res, i, 
						toScriptable(cx, (DataStruct<?>)val, topScope));
				}
			}
		} else {
			res = rhino.newObject(cx, topScope);
			DataObject o = (DataObject)source;
			for(String k : o.getKeys()) {
				Object val = o.get(k);
				if(structHelper.isPrimitiveOrNull(val)) {
					rhino.putProperty(res, k, convertPrimitiveToRhino(val));
				} else {
					rhino.putProperty(res, k, 
						toScriptable(cx, (DataStruct<?>)val, topScope));
				}
			}
		}
		return res;
	}

	private Object convertPrimitiveToRhino(Object val) {
		return val;//TODO date number
	}

	public DataStruct<?> fromScriptable(Scriptable s) {
		DataStruct<?> res;
		if(rhino.isArray(s)) {
			res = new DataArrayImpl();
			NativeArray a = (NativeArray)s;//TODO unsafe
			final int len = (int)a.getLength();
			for(int i = 0; i < len; i++) {
				Object val = rhino.getProperty(s, i);
				if(structHelper.isPrimitiveOrNull(val)) {
					((DataArray)res).put(i, val);
				} else if(rhino.isUndefined(val)) {
				} else {
					((DataArray)res).put(i, fromScriptable((Scriptable)val));
				}
			}
		} else {
			res = new DataObjectImpl();
			for(Object k : s.getIds()) {
				Object val = rhino.getProperty(s, k);
				if(structHelper.isPrimitiveOrNull(val)) {
					((DataObject)res).put((String)k, val);
				} else if(rhino.isUndefined(val)) {
				} else {
					((DataObject)res).put((String)k, fromScriptable((Scriptable)val));
				}
			}
		}
		return res;
	}
}
