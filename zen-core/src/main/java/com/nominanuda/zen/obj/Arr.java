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

import static com.nominanuda.zen.obj.JsonDeserializer.JSON_DESERIALIZER;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

import javax.annotation.Nullable;

public interface Arr extends TArr<Object> {

	public static Arr make(Object...vals) {
		ArrImpl arr = new ArrImpl();
		if(vals != null) {
			for(int i = 0; i < vals.length; i++) {
				arr.push(vals[i]);
			}
		}
		return arr;
	}

	@SuppressWarnings("unchecked")
	public static <T> TArr<T> makeTyped(Class<T> cl, Object...vals) {
		return (TArr<T>)make(vals);
	}

	default Arr pushArr() {
		Arr a = newArr();
		return (Arr)push(a);
	}

	default Obj pushObj() {
		Obj o = newObj();
		return (Obj) push(o);
	}

	@SuppressWarnings("unchecked")
	default <K> TArr<K> of(Class<K> klass) {
		return (TArr<K>)this;
	}

	default TArr<Obj> ofObj() {
		return of(Obj.class);
	}
	public static TArr<Obj> ofObj(@Nullable Arr a) {
		return (a != null ? a : make()).ofObj();
	}

	default TArr<Arr> ofArr() {
		return of(Arr.class);
	}
	public static TArr<Arr> ofArr(@Nullable Arr a) {
		return (a != null ? a : make()).ofArr();
	}

	default TArr<String> ofStr() {
		return of(String.class);
	}
	public static TArr<String> ofStr(@Nullable Arr a) {
		return (a != null ? a : make()).ofStr();
	}

	default Obj obj(int idx) throws ClassCastException {
		return (Obj)fetch(idx);
	}

	default Obj addObj() {
		Obj o = Obj.make();
		add(o);
		return o;
	}

	default Arr addArr() {
		Arr arr = newArr();
		add(arr);
		return arr;
	}

	static Arr fromList(Collection<?> c) {
		Arr a = make();
		StruUtils.deepCopy(c, a);
		return a;
	}

	default Obj getObj(int i) {
		return (Obj)fetch(i);
	}

	public static Arr parse(String r) {
		return (Arr)JSON_DESERIALIZER.deserialize(r);
	}

	public static Arr parse(InputStream is) {
		return (Arr)JSON_DESERIALIZER.deserialize(is);
	}

	public static Arr parse(Reader r) {
		return (Arr)JSON_DESERIALIZER.deserialize(r);
	}

}
