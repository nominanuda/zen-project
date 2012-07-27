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
package com.nominanuda.web.mvc;

import java.net.URL;

import org.apache.http.HttpRequest;

import com.nominanuda.lang.Check;

public class PrefixReplaceURLStreamer extends URLStreamer {
	private String strip = "";
	private int stripLen = 0;
	private String replace;

	@Override
	protected URL getURL(HttpRequest request) throws IllegalArgumentException {
		String reqURI = request.getRequestLine().getUri();
		Check.illegalargument.assertTrue(reqURI.startsWith(strip));
		try {
			return new URL(replace + reqURI.substring(stripLen));
		} catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setStrip(String strip) {
		this.strip = strip;
		this.stripLen = this.strip.length();
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}

}
