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
package com.nominanuda.web.http;

import java.util.Date;

import javax.annotation.concurrent.Immutable;

import org.apache.http.cookie.Cookie;

@Immutable
public class NameValueCookie implements Cookie {
	private final String name;
	private final String value;

	public NameValueCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}

	public String getComment() {
		return null;
	}

	public String getCommentURL() {
		return null;
	}

	public Date getExpiryDate() {
		return null;
	}

	public boolean isPersistent() {
		return false;
	}

	public String getDomain() {
		return null;
	}

	public String getPath() {
		return null;
	}

	public int[] getPorts() {
		return null;
	}

	public boolean isSecure() {
		return false;
	}

	public int getVersion() {
		return 0;
	}

	public boolean isExpired(Date date) {
		return false;
	}
}
