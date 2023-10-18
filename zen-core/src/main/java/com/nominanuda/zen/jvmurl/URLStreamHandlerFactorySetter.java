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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class URLStreamHandlerFactorySetter {
	private static Field streamHandlerFactoryField;
	static {
		final Field[] fields = URL.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field current = fields[i];
			if (Modifier.isStatic(current.getModifiers()) && current.getType().equals(URLStreamHandlerFactory.class)) {
				current.setAccessible(true);
				streamHandlerFactoryField = current;
			}
		}
		if (streamHandlerFactoryField == null) {
			throw new RuntimeException("Unable to detect static field in the URL class for the URLStreamHandlerFactory.");
		}
	}

	public static void setURLStreamHandlerFactory(URLStreamHandlerFactory factory) throws Exception {
		try {
			URL.setURLStreamHandlerFactory(new DelegatingURLStreamHandlerFactory(factory, null));
		} catch (Error err) {
			URLStreamHandlerFactory currentFactory = (URLStreamHandlerFactory) streamHandlerFactoryField.get(null);
			streamHandlerFactoryField.set(null, new DelegatingURLStreamHandlerFactory(factory, currentFactory));
		}
	}

	private static class DelegatingURLStreamHandlerFactory implements URLStreamHandlerFactory {
		private final URLStreamHandlerFactory factory;
		private final URLStreamHandlerFactory parent;

		public DelegatingURLStreamHandlerFactory(URLStreamHandlerFactory factory, URLStreamHandlerFactory parent) {
			this.parent = parent;
			this.factory = factory;
		}

		public URLStreamHandler createURLStreamHandler(String protocol) {
			URLStreamHandler handler = factory.createURLStreamHandler(protocol);
			if (handler == null && this.parent != null) {
				handler = this.parent.createURLStreamHandler(protocol);
			}
			return handler;
		}

	}
}
