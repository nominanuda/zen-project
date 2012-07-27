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

import java.util.regex.Pattern;

import com.nominanuda.code.Nullable;

public interface DataStruct {
	Pattern VALID_OBJ_KEY = Pattern.compile("[\\$_A-Za-z][\\$_A-Za-z0-9]*");
	@Nullable DataStruct getParent();	
	@Nullable /*root returns itself */ DataStruct getRoot();
	//DataStruct<K> cloneStruct();

	boolean isArray();
	boolean isObject();
	String getType();//object or adrray
	DataArray asArray() throws ClassCastException;
	DataObject asObject() throws ClassCastException;
	
}
