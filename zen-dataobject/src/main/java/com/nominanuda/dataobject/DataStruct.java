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
package com.nominanuda.dataobject;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.nominanuda.code.Nullable;

public interface DataStruct<K extends Object> {
	String OBJECT = "Object";
	String ARRAY = "Array";
	Pattern VALID_OBJ_KEY = Pattern.compile("[_A-Za-z][_A-Za-z0-9]*");

	@Nullable DataStruct<?> getParent();	
	@Nullable /*should root return itself ?*/ DataStruct<?> getRoot();//miki
	DataStruct<K> cloneStruct();
	boolean isArray();
	boolean isObject();
	String getType();//Object or Array
	DataArray asArray() throws ClassCastException;
	DataObject asObject() throws ClassCastException;
	Iterator<K> keyIterator();
	@Nullable Object get(K key);
	Object getStrict(K key);
	Object put(K key, @Nullable Object o);
	@Nullable Object remove(K key);
	boolean exists(K k);
	DataArray putNewArray(K key);
	DataObject putNewObject(K key);
	List<K> getKeys();
	boolean isPrimitiveOrNull(@Nullable Object o);
	void setPathProperty(String path, @Nullable Object value);
	void setOrPushPathProperty(String path, @Nullable  Object value);
	void setProperty(Object key, @Nullable Object value);
	void setOrPushProperty(Object key, @Nullable Object value);
	Object getPathSafe(String path);
	@Nullable String getString(K key) throws ClassCastException;
	@Nullable Number getNumber(K key) throws ClassCastException;
	@Nullable Boolean getBoolean(K key) throws ClassCastException;
	@Nullable DataObject getObject(K key) throws ClassCastException;
	@Nullable DataArray getArray(K key) throws ClassCastException;
	@Nullable String getPathSafeString(String path) throws ClassCastException;
	@Nullable Number getPathSafeNumber(String path) throws ClassCastException;
	@Nullable Boolean getPathSafeBoolean(String path) throws ClassCastException;
	@Nullable DataObject getPathSafeObject(String path) throws ClassCastException;
	@Nullable DataArray getPathSafeArray(String path) throws ClassCastException;

}
