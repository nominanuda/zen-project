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

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.lang.Check;
import com.nominanuda.urispec.URISpec;

public class URITransformerURLStreamer extends URLStreamer {
	private URISpec<DataObject> match;
	private URISpec<DataObject> template;

	@Override
	protected URL getURL(HttpRequest request) throws IllegalArgumentException {
		String reqURI = request.getRequestLine().getUri();
		try {
			DataObject o = match.match(reqURI);
			Check.illegalargument.assertNotNull(o);
			return getUrlResolver().url(template.template(o));
		} catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	public void setMatch(String match) {
		this.match = new URISpec<DataObject>(
				match, new DataObjectStringModelAdapter());
	}
	public void setTemplate(String template) {
		this.template = new URISpec<DataObject>(
				template, new DataObjectStringModelAdapter());
	}

}
