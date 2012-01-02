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
package com.nominanuda.rhino;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
//miki
import javax.swing.text.html.Option;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class JCoffeeScriptCompiler {
	private final Scriptable globalScope;

	public JCoffeeScriptCompiler() {
		this(Collections.<Option> emptyList());
	}

	public JCoffeeScriptCompiler(Collection<Option> options) {
		InputStream inputStream = getClass().getResourceAsStream("coffee-script.js");
		try {
			try {
				Reader reader = new InputStreamReader(inputStream, "UTF-8");
				try {
					Context context = Context.enter();
					context.setOptimizationLevel(-1); // Without this, Rhino
														// hits a 64K bytecode
														// limit and fails
					try {
						globalScope = context.initStandardObjects();
						context.evaluateReader(globalScope, reader,
								"coffee-script.js", 0, null);
					} finally {
						Context.exit();
					}
				} finally {
					reader.close();
				}
			} catch (UnsupportedEncodingException e) {
				throw new Error(e); // This should never happen
			} finally {
				if(inputStream != null) {
					inputStream.close();
				}
			}
		} catch (IOException e) {
			throw new Error(e); // This should never happen
		}
	}

	public String compile(String coffeeScriptSource)
			throws JavaScriptException {
		Context context = Context.enter();
		try {
			Scriptable compileScope = context.newObject(globalScope);
			compileScope.setParentScope(globalScope);
			compileScope.put("coffeeScriptSource", compileScope,
					coffeeScriptSource);
			try {
				return (String) context.evaluateString(
					compileScope,
					"CoffeeScript.compile(coffeeScriptSource, {bare: true});",
					"JCoffeeScriptCompiler", 0, null);
			} catch (JavaScriptException e) {
				throw e;
			}
		} finally {
			Context.exit();
		}
	}

}
