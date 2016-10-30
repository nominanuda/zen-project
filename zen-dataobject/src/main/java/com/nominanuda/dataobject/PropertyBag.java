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

import com.nominanuda.code.Nullable;

public interface PropertyBag<K> extends DataStruct {
	Iterator<K> keyIterator();
	@Nullable Object get(K key);
	Object getStrict(K key) throws NullPointerException;
	Object put(K key, @Nullable Object o);
	@Nullable Object remove(K key);
	boolean exists(K k);
	DataArray putNewArray(K key);
	DataObject putNewObject(K key);
	List<K> getKeys();
	@Deprecated
	boolean isPrimitiveOrNull(@Nullable Object o);
	void setPathProperty(String path, @Nullable Object value);
	void setOrPushPathProperty(String path, @Nullable  Object value);
	//void setProperty(Object key, @Nullable Object value);
	void setOrPushProperty(Object key, @Nullable Object value);
	Object getPathSafe(String path);
	Object getPathSafe(String... pathBits);
	@Nullable String getString(K key) throws ClassCastException;
	@Nullable Number getNumber(K key) throws ClassCastException;
	@Nullable Boolean getBoolean(K key) throws ClassCastException;
	@Nullable DataObject getObject(K key) throws ClassCastException;
	@Nullable DataArray getArray(K key) throws ClassCastException;
	@Nullable String getPathSafeString(String path) throws ClassCastException;
	@Nullable String getPathSafeString(String... pathBits) throws ClassCastException;
	@Nullable Number getPathSafeNumber(String path) throws ClassCastException;
	@Nullable Number getPathSafeNumber(String... pathBits) throws ClassCastException;
	@Nullable Boolean getPathSafeBoolean(String path) throws ClassCastException;
	@Nullable Boolean getPathSafeBoolean(String... pathBits) throws ClassCastException;
	@Nullable DataObject getPathSafeObject(String path) throws ClassCastException;
	@Nullable DataObject getPathSafeObject(String... pathBits) throws ClassCastException;
	@Nullable DataArray getPathSafeArray(String path) throws ClassCastException;
	@Nullable DataArray getPathSafeArray(String... pathBits) throws ClassCastException;
	@Nullable Long getLong(K key) throws ClassCastException;
	@Nullable Double getDouble(K key) throws ClassCastException;

	String getStrictString(K key) throws ClassCastException, NullPointerException;
	Long getStrictLong(K key) throws ClassCastException, NullPointerException;
	Double getStrictDouble(K key) throws ClassCastException, NullPointerException;
	Boolean getStrictBoolean(K key) throws ClassCastException, NullPointerException;
	DataObject getStrictObject(K key) throws ClassCastException, NullPointerException;
	DataArray getStrictArray(K key) throws ClassCastException, NullPointerException;

	Long putLong(K key, @Nullable Long o);
	Double putDouble(K key, @Nullable Double o);
	String putString(K key, @Nullable String o);
	Boolean putBoolean(K key, @Nullable Boolean o);
	DataObject putObject(K key, @Nullable DataObject o);
	DataArray putArray(K key, @Nullable DataArray o);


	
}
