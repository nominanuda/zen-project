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
package com.nominanuda.dataview;

import java.util.Collection;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;

public class ObjPropertyReader implements PropertyReader<Obj> {
	public Collection<String> readableProps(Obj m) {
		return m.keySet();
	}
	public Object read(Obj m, String k) {
		return m.get(k);
	}
	public boolean accepts(Object o) {
		return Check.notNull(o) instanceof Obj;
	}
	public boolean hasProp(Obj o, String k) {
		return o.exists(k);
	}
}