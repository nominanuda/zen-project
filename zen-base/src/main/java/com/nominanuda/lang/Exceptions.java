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
package com.nominanuda.lang;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import com.nominanuda.code.ThreadSafe;

@ThreadSafe
public class Exceptions {
	public static String toStackTrace(Exception e) {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(buf);
			e.printStackTrace(writer);
			writer.flush();
			return new String(buf.toByteArray(), "UTF-8");
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}
