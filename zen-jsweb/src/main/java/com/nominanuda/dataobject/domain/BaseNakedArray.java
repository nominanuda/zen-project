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
package com.nominanuda.dataobject.domain;

import java.util.Iterator;
import java.util.List;

import com.nominanuda.dataobject.AbstractDataStruct;
import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;


public class BaseNakedArray extends AbstractDataStruct<Integer> implements NakedArray {
	private final DataArray model;

	public DataStruct<Integer> getModel() {
		return model;
	}
	public BaseNakedArray(DataArray o) {
		super(null);
		model = o;
	}
	public Object add(Object val) {
		return model.add(val);
	}
	public DataObject addNewObject() {
		return model.addNewObject();
	}
	public DataArray addNewArray() {
		return model.addNewArray();
	}
	public int getLength() {
		return model.getLength();
	}
	public int compact() {
		return model.compact();
	}
	public DataStruct<?> getParent() {
		return model.getParent();
	}
	public DataStruct<?> getRoot() {
		return model.getRoot();
	}
	public DataStruct<Integer> cloneStruct() {
		return model.cloneStruct();
	}
	public boolean isArray() {
		return model.isArray();
	}
	public boolean isObject() {
		return model.isObject();
	}
	public String getType() {
		return model.getType();
	}
	public DataArray asArray() throws ClassCastException {
		return model.asArray();
	}
	public DataObject asObject() throws ClassCastException {
		return model.asObject();
	}
	public Iterator<Integer> keyIterator() {
		return model.keyIterator();
	}
	public Object get(Integer key) {
		return model.get(key);
	}
	public Object getStrict(Integer key) {
		return model.getStrict(key);
	}
	public Object put(Integer key, Object o) {
		return model.put(key, o);
	}
	public Object remove(Integer key) {
		return model.remove(key);
	}
	public boolean exists(Integer k) {
		return model.exists(k);
	}
	public DataArray putNewArray(Integer key) {
		return model.putNewArray(key);
	}
	public DataObject putNewObject(Integer key) {
		return model.putNewObject(key);
	}
	public List<Integer> getKeys() {
		return model.getKeys();
	}
	public boolean isPrimitiveOrNull(Object o) {
		return model.isPrimitiveOrNull(o);
	}
	public void setPathProperty(String path, Object value) {
		model.setPathProperty(path, value);
	}
	public void setOrPushPathProperty(String path, Object value) {
		model.setOrPushPathProperty(path, value);
	}
	public void setProperty(Object key, Object value) {
		model.setProperty(key, value);
	}
	public void setOrPushProperty(Object key, Object value) {
		model.setOrPushProperty(key, value);
	}
	public Object getPathSafe(String path) {
		return model.getPathSafe(path);
	}
	public String getString(Integer key) throws ClassCastException {
		return model.getString(key);
	}
	public Number getNumber(Integer key) throws ClassCastException {
		return model.getNumber(key);
	}
	public Boolean getBoolean(Integer key) throws ClassCastException {
		return model.getBoolean(key);
	}
	public DataObject getObject(Integer key) throws ClassCastException {
		return model.getObject(key);
	}
	public DataArray getArray(Integer key) throws ClassCastException {
		return model.getArray(key);
	}
	public String getPathSafeString(String path) throws ClassCastException {
		return model.getPathSafeString(path);
	}
	public Number getPathSafeNumber(String path) throws ClassCastException {
		return model.getPathSafeNumber(path);
	}
	public Boolean getPathSafeBoolean(String path) throws ClassCastException {
		return model.getPathSafeBoolean(path);
	}
	public DataObject getPathSafeObject(String path) throws ClassCastException {
		return model.getPathSafeObject(path);
	}
	public DataArray getPathSafeArray(String path) throws ClassCastException {
		return model.getPathSafeArray(path);
	}
	public Iterator<Object> iterator() {
		return model.iterator();
	}
	@Override
	protected DataStruct<Integer> cloneStruct(AbstractDataStruct<?> parent) {
		return new DataStructHelper().clone(this, parent);
	}

}
