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

import static com.nominanuda.zen.common.Str.STR;

public class InvalidApikeyException extends RuntimeException {
	private static final long serialVersionUID = -3024120403316011063L;
	private boolean wellFormed = false;
	private boolean valid = false;

	public static InvalidApikeyException notWellFormed() {
		InvalidApikeyException ex = new InvalidApikeyException();
		ex.wellFormed = false;
		return ex;
	}

	public static InvalidApikeyException notWellFormed(Exception e) {
		InvalidApikeyException ex = new InvalidApikeyException(e);
		ex.wellFormed = false;
		return ex;
	}

	public static InvalidApikeyException notValid() {
		InvalidApikeyException ex = new InvalidApikeyException();
		ex.wellFormed = true;
		return ex;
	}

	private InvalidApikeyException() {
		super();
	}
	private InvalidApikeyException(Exception e) {
		super(e);
	}

	public boolean isNotWellFormed() {
		return wellFormed;
	}

	public boolean isNotValid() {
		return valid;
	}

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		return STR.nullOrEmpty(msg) ? "unauthorized" : msg;
	}

}
