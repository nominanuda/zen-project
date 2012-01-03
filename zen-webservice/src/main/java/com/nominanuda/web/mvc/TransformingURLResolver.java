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

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.nominanuda.lang.Check;
import com.nominanuda.uri.URLStreamHandlerFactoryResolver;
import com.nominanuda.urispec.URITransformer;

public class TransformingURLResolver extends URLStreamHandlerFactoryResolver {
	private Map<String, URITransformer> transformers = new ConcurrentHashMap<String,URITransformer>();

	@Override
	public URL url(String url) throws IllegalArgumentException {
		URI uri = URI.create(url);
		if(uri.isAbsolute()) {
			String scheme = uri.getScheme();
			for(Entry<String, URITransformer> tx : transformers.entrySet()) {
				if(scheme.equals(tx.getKey())) {
					String res = tx.getValue().transform(url);
					Check.illegalargument.notNullOrEmpty(res);
					return super.url(res);
				}
			}
		}
		return super.url(url);
	}
	public void setTransformers(Map<String, URITransformer> txs) {
		this.transformers.putAll(txs);
	}
	public void putTransformer(String scheme, URITransformer tx) {
		this.transformers.put(scheme, tx);
	}
}
