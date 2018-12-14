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
package com.nominanuda.password;

import java.security.SecureRandom;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Str;

public abstract class SecurePassword {
	private static final int SALT_BYTE_SIZE = 24;
	private boolean doSalt = true;
	
	public SecurePassword salt(boolean doSalt) {
		this.doSalt = doSalt;
		return this;
	}
	
	public String hash(@Nullable String password) {
		return (password != null ? hash(password.getBytes(Str.UTF8)) : null);
	}
	public abstract String hash(byte[] password);
	
	public boolean check(@Nullable String password, @Nullable String hash) {
		return (password != null && hash != null) ? check(password.getBytes(Str.UTF8), hash) : false;
	}
	public abstract boolean check(byte[] password, String hash);
	
	protected final byte[] salt() {
		if (doSalt) {
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[SALT_BYTE_SIZE];
			random.nextBytes(salt);
			return salt;
		}
		return new byte[0];
	}
}
