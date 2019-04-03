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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nominanuda.zen.common.Check;

public class ApikeyConfigurator {
	private FieldConfig fingerprint;
	private FieldConfig user;
	private FieldConfig time;
	private FieldConfig roles;
	List<FieldConfig> customFields = new LinkedList<>();
	
	
	public ApikeyConfigurator() { // default
	};
	public ApikeyConfigurator(String authority) {
		setAll(authority);
	}
	public ApikeyConfigurator(String authority, Map<String, Class<?>> fields) {
		this(authority);
		List<FieldConfig> flist = new ArrayList<>();
		for (String name : fields.keySet()) {
			flist.add(new FieldConfig(name, authority, fields.get(name)));
		}
		setFields(flist);
	}
	public ApikeyConfigurator(String authority, List<FieldConfig> fields) {
		this(authority);
		setFields(fields);
	}
	public ApikeyConfigurator(String fprintAuth, String userAuth, String timeAuth, String rolesAuth) {
		setFingerprint(fprintAuth);
		setUser(userAuth);
		setTime(timeAuth);
		setRoles(rolesAuth);
	}
	public ApikeyConfigurator(String fprintAuth, String userAuth, String timeAuth, String rolesAuth, List<FieldConfig> fields) {
		this(fprintAuth, userAuth, timeAuth, rolesAuth);
		setFields(fields);
	}
	
	
	public ApikeyConfigurator addField(FieldConfig cfg) {
		Check.illegalargument.assertFalse(
			Arrays.asList(StandardField.values()).contains(cfg.name), 
			cfg.name + "not allowed as a custom segment name");
		customFields.add(cfg);
		return this;
	}
	public ApikeyConfigurator addField(String name, String authority, Class<?> dataType) {
		return addField(new FieldConfig(name, authority, dataType));
	}
	public ApikeyConfigurator addField(String name, String authority, Class<?> dataType, boolean multi) {
		return addField(new FieldConfig(name, authority, dataType, multi));
	}
	
	public ApikeyConfigurator setFields(List<FieldConfig> l) {
		customFields.clear();
		for (FieldConfig fc : l) {
			addField(fc);
		}
		return this;
	}
	
	
	public FieldMap getFieldMap(KeyRing keyRing) {
		return new FieldMap(getAllFields(), keyRing);
	}
	
	public FieldConfig getStandardField(StandardField sf) {
		switch (sf) {
		case fingerprint: return this.fingerprint;
		case user: return this.user;
		case time: return this.time;
		case roles: return this.roles;
		default: throw new IllegalArgumentException(sf.name()); 
		}
	}

	public List<FieldConfig> getCustomFields() {
		return customFields;
	}
	private List<FieldConfig> getAllFields() {
		List<FieldConfig> fieldMap = new LinkedList<FieldConfig>();
		fieldMap.add(fingerprint);
		fieldMap.add(user);
		fieldMap.add(time);
		fieldMap.add(roles);
		for(FieldConfig fc : customFields) {
			fieldMap.add(fc);
		}
		return fieldMap;
	}
	
	
	/* setters */

	public ApikeyConfigurator setFingerprint(String authority) {
		fingerprint = new FieldConfig(StandardField.fingerprint.name(), authority, String.class, false);
		return this;
	}
	public ApikeyConfigurator setUser(String authority) {
		user = new FieldConfig(StandardField.user.name(), authority, String.class, false);
		return this;
	}
	public ApikeyConfigurator setTime(String authority) {
		time = new FieldConfig(StandardField.time.name(), authority, Time.class, false);
		return this;
	}
	public ApikeyConfigurator setRoles(String authority) {
		roles = new FieldConfig(StandardField.roles.name(), authority, String.class, true);
		return this;
	}
	public ApikeyConfigurator setAll(String authority) {
		setFingerprint(authority);
		setUser(authority);
		setTime(authority);
		setRoles(authority);
		return this;
	}
}
