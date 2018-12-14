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

import static com.nominanuda.apikey.StandardField.fingerprint;
import static com.nominanuda.apikey.StandardField.roles;
import static com.nominanuda.apikey.StandardField.time;
import static com.nominanuda.apikey.StandardField.user;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Arr;


public class Apikey {
	private Segments segments;

	public Apikey(Segments segments) {
		this.segments = segments;
	}

	public String getFingerprint() {
		Field s = segments.getField(fingerprint.name());
		return (String)s.get();
	}

	public Object getField(String name) {
		Field s = segments.getField(name);
		return s.get();
	}

	public long getStart() {
		Time t = castValue(time);
		return t.start();
	}
	public long getDuration() {
		Time t = castValue(time);
		return t.duration();
	}
	public boolean isWithinValidityPeriod(long pointInInTime) {
		return pointInInTime >= getStart() && pointInInTime <= getExpiryTime();
	}
	
	public long getExpiryTime() {
		return getStart() + getDuration();
	}
	public boolean notExpired() {
		return isWithinValidityPeriod(now());
	}
	public boolean isExpired() {
		return !notExpired();
	}

	public String getUser() {
		return castValue(user);
	}
	public Arr getRolesArr() {
		return castValue(roles);
	}
	public List<String> getRoles() {
		return getRolesArr().asListOf(String.class);
	}
	public boolean isUserInRole(@Nullable String role) {
		return role != null && getRolesArr().contains(role);
	}

	public Apikey setField(String name, Object value) {
		segments.setField(name, value);
		return this;
	}

	public Apikey extend(long amount, TimeUnit u) {
		Time t = (Time)getField(time.name());
		long millis = u.toMillis(amount);
		t.duration(millis + t.duration());
		setField(time.name(), t);
		return this;
	}
	public Apikey refresh() {
		long soFar = now() - getStart();
		Check.illegalstate.assertTrue(soFar > 0);
		return extend(soFar, TimeUnit.MILLISECONDS);
	}
	
	public String serialize() {
		return segments.serialize();
	}

	
	/* utils */
	
	private long now() {
		return System.currentTimeMillis();
	}

	private <T> T castValue(StandardField name) {
		@SuppressWarnings("unchecked")
		T t = (T)segments.getField(name.name()).get();
		return t;
	}
	
}
