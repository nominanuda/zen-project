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

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Check;

public class PluggableURLStreamHandlerFactory implements URLStreamHandlerFactory {
	private static Map<String,URLStreamHandler> resolvedHandlers = new ConcurrentHashMap<String,URLStreamHandler>();
	private Map<String, URLStreamHandler> plugins = new ConcurrentHashMap<String, URLStreamHandler>();
	
	public URLStreamHandler createURLStreamHandler(final String protocol) {
		URLStreamHandler handler = resolvedHandlers.get(protocol);
		if (handler != null) {
			return handler;
		} else if(plugins.containsKey(protocol)) {
			handler = plugins.get(protocol);
			resolvedHandlers.put(protocol, handler);
			return handler;
		} else {
			try {
				if(isRecursiveCall(protocol)) {
					throw new RuntimeException("got an unespected recursive call");
				} else {
					initRecursionCheck(protocol);
					Class<? extends URLStreamHandler> clazz = findURLStreamHandlerClass(protocol);
					if(clazz != null) {
						handler = clazz.newInstance();
						resolvedHandlers.put(protocol, handler);
						return handler;
					} else {
						return null;
					}
				}
			} catch (Exception e) {
				return null;
			} finally {
				disposeRecursionCheck();
			}
		}
	}

	private @Nullable Class<? extends URLStreamHandler> findURLStreamHandlerClass(String protocol) {
		ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
		for(String pkg : findHandlerPkgs()) {
			try {
				String classname = pkg + "." + protocol + ".Handler";
				Class<?> type = null;
				try {
					type = ctxLoader.loadClass(classname);
				} catch (ClassNotFoundException e) {
					type = Class.forName(classname);
				}
				if (type != null) {
					return type.asSubclass(URLStreamHandler.class);
				}
			} catch (Throwable ignore) {}
		}
		return null;
	}

	public void installToJvm() throws Exception {
		URLStreamHandlerFactorySetter
			.setURLStreamHandlerFactory(this);
	}
	public void setPlugins(Map<String, URLStreamHandler> plugins) {
		this.plugins.putAll(plugins);
	}
	public void addPlugin(String protocol, URLStreamHandler handler) {
		this.plugins.put(protocol, handler);
	}

	//packages management
	private Set<String> pkgs = new CopyOnWriteArraySet<>();
	{
		pkgs.add("sun.net.www.protocol");
	}
	private String sysPkgs = "";
	private Set<String> findHandlerPkgs() {
		String sysProp = Check.ifNullOrBlank(
				System.getProperty("java.protocol.handler.pkgs"), "");
		if (!sysPkgs.equals(sysProp)) {
			pkgs.addAll(Arrays.asList(sysProp.split("\\|")));
			sysPkgs = sysProp;
		}
		return pkgs;
	}
	public void addSearchPackage(String pkg) {
		pkgs.add(pkg);
	}
	//recursion process manage
	private static ThreadLocal<String> currentSearchProto = new ThreadLocal<String>();
	private void disposeRecursionCheck() {
		currentSearchProto.remove();
	}

	private void initRecursionCheck(final String protocol) {
		currentSearchProto.set(protocol);
	}

	private boolean isRecursiveCall(final String protocol) {
		String prevProtocol = currentSearchProto.get();
		boolean recursiveCall = (prevProtocol != null && prevProtocol.equals(protocol));
		return recursiveCall;
	}


}
