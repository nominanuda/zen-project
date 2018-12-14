/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.apikey;

import java.util.Map.Entry;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;

public class FieldConfig implements Entry<String, FieldConfig>{
	public static final String PUBLIC = "public";
	public final String name;
	public final String authority;
	public final Class<?> dataType;
	public final boolean multi;

	public FieldConfig(String name, String authority, Class<?> dataType, boolean multi) {
			Check.illegalargument.assertTrue(
				ObjWrapper.class.isAssignableFrom(dataType)
			||	Obj.class.isAssignableFrom(dataType)
			||	Number.class.isAssignableFrom(dataType)
			||	String.class.equals(dataType)
			||	Boolean.class.equals(dataType), "field dataType isn't an allowed one");
		this.name = name;
		this.authority = authority;
		this.dataType = dataType;
		this.multi = multi;
	}
	public FieldConfig(String name, String authority, Class<?> dataType) {
		this(name, authority, dataType, false);
	}

	public String getSortingKey() {
		return authority;
	}

	@Override
	public String getKey() {
		return name;
	}

	@Override
	public FieldConfig getValue() {
		return this;
	}

	@Override
	public FieldConfig setValue(FieldConfig value) {
		throw new UnsupportedOperationException();
	}
	
}
