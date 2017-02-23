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
package com.nominanuda.zen.jvmurl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import com.nominanuda.zen.common.Check;

public class URLStreamHandlerFactoryResolver {
	private URLStreamHandlerFactory handlerFactory;
	public URL url(String url) throws IllegalArgumentException{
		Check.notNull(url);
		URI uri = URI.create(url);
		try {
			if(handlerFactory == null) {
				return uri.toURL();
			} else {
				URLStreamHandler h = handlerFactory
					.createURLStreamHandler(uri.getScheme());
				return new URL(null, uri.toString(), h);
			}
		} catch(MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setHandlerFactory(URLStreamHandlerFactory f) {
		handlerFactory = f;
	}
}
