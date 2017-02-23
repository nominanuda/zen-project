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

import javax.annotation.Nullable;

import com.nominanuda.zen.stereotype.Value;

public interface Any extends JixSrc, Value {

//	public static @Nullable Object toJavaObjModel(Any a) {
//		return a.isVal() ? a.asVal().get() : /*(Stru)*/a;
//	}
//
	public static Any toStruObjModel(@Nullable Object v) {
		return v == null ? Val.NULL : v instanceof Stru ? (Stru)v : Val.of(v);
	}

	public default boolean isVal() {
		return this instanceof Val;
	}

	public JsonType getType();

	@Override
	public Any copy();

	public default Val asVal() throws ClassCastException {
		return (Val)this;
	}

	public default boolean isObj() {
		return this instanceof Obj;
	}

	public default Obj asObj() throws ClassCastException {
		return (Obj)this;
	}

	public default boolean isArr() {
		return this instanceof Arr;
	}

	public default Arr asArr() throws ClassCastException {
		return (Arr)this;
	}

	public @Nullable Object toJavaObjModel();


//	public default @Nullable Object toJavaObjModel() {
//		return toJavaObjModel(this);
//	}
}
