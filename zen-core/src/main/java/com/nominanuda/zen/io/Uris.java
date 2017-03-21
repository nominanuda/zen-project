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
package com.nominanuda.zen.io;

import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

@ThreadSafe
public class Uris {
	public static final Uris URIS = ScopedSingletonFactory.getInstance().buildJvmSingleton(Uris.class);

	public String getLastPathSegment(String path) {
		String[] bits = path.split("/");
		return bits[bits.length - 1];
	}

	public String pathJoin(String... segments) {
		StringBuilder sb = new StringBuilder();
		boolean lastEndsWithSlash = false;
		boolean first = true;
		for (String s : segments) {
			if (first) {
				sb.append(s);
				first = false;
			} else {
				if ((! lastEndsWithSlash) && (! s.startsWith("/"))) {
					sb.append("/");
					sb.append(s);
				} else if (lastEndsWithSlash && s.startsWith("/")) {
					sb.append(s.substring(1));
				} else {
					sb.append(s);
				}
			}
			lastEndsWithSlash = s.endsWith("/");
		}
		return sb.toString();
	}
}
