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
package com.nominanuda.urispec;

import java.util.List;

//it is implementor responsibility to decode params in write methods (sets and pushes)
public interface StringModelAdapter<SM> {
	SM createStringModel();
	boolean validateModel(Object m);
	void push(SM model, String key, String val);
	void set(SM model, String key, String val);
	void set(SM model, String key, List<String> val);
	void setAll(SM from, SM to);
	void pushAll(SM from, SM to);
	List<String> getAsList(SM model, String key);
	String getFirst(SM model, String key);
}