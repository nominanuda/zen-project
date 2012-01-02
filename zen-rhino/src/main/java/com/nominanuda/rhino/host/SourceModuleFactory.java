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
package com.nominanuda.rhino.host;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.Scriptable;

import com.nominanuda.code.Nullable;
import com.nominanuda.io.IOHelper;

public class SourceModuleFactory implements ModuleFactory {
	private static final IOHelper io = new IOHelper();
	private static final RhinoHelper rhino = new RhinoHelper();
	private URI baseUri = null;
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public @Nullable Object create(String key, Scriptable thisObj, Scriptable scope,
			Context context) throws Exception {
		URI uri = baseUri == null ? URI.create(key) : baseUri.resolve(key);
		Scriptable moduleScope = rhino.newObject(context, scope);
		try {
			Reader reader = new InputStreamReader(uri.toURL().openStream(), UTF8);
			Reader src = new StringReader("" + io.readAndClose(reader) + "");
			rhino.putProperty(moduleScope, "exports", rhino.newObject(context, moduleScope));
			rhino.putProperty(moduleScope, "require", rhino.getProperty(scope, "require"));
			rhino.evaluateReader(context, src, uri.toString(), moduleScope);
		} catch(IllegalArgumentException e) {
			return null;
		} catch(IOException e) {
			return null;
		}
		Object result = rhino.getProperty(moduleScope, "exports");
		rhino.deleteProperty(moduleScope, "require");
		rhino.deleteProperty(moduleScope, "exports");
		return result;
	}

	public void setBaseUri(URI baseUri) {
		this.baseUri = baseUri;
	}

}
