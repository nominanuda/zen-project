/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.stereotype;

import java.io.IOException;
import java.io.UncheckedIOException;

public class NotFoundException extends UncheckedIOException {
	private static final long serialVersionUID = 7073227511145629335L;

	public NotFoundException(String message, IOException cause) {
		super(message, cause);
	}
	public NotFoundException(IOException cause) {
		super(cause);
	}
	public NotFoundException(String message) {
		super(new IOException(message));
	}

}
