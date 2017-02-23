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

import static com.nominanuda.rhino.StruScriptableConvertor.DSS_CONVERTOR;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ToScriptableConvertor;

import com.nominanuda.zen.obj.Stru;

public class StruConvertor implements ToScriptableConvertor {
	public Scriptable convert(Context cx, Scriptable prototypeSearchScope, Object o) {
		return DSS_CONVERTOR.toScriptable(cx, (Stru)o, prototypeSearchScope);
	}

	public boolean canConvert(Object obj) {
		return obj != null && obj instanceof Stru;
	}

}
