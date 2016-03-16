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

import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;


public class RhinoTestCase extends AbsRhinoTestCase {
	protected RhinoEmbedding rhinoEmbedding;
	protected ScriptableObject testScope;
	protected Context testContext;
	
	@Before
	public void setup() throws Exception {
		rhinoEmbedding = buildRhinoEmbedding();
		testContext = rhinoEmbedding.enterContext();
		testScope = RHINO.protocloneScriptable(testContext, RHINO.createTopScope(testContext, true));
		RHINO.putProperty(testScope, "require", buildRhinoRequire());
		//URL url = new URL("classpath:com/nominanuda/rhino/JUnit.js");
		//rhino.evaluateURL(testContext, url, testScope);
		onSetup();
	}

	protected void onSetup() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
		Context.exit();
	}
	
	protected Object runJSTestCase(URL url) throws Exception {
		try {
			return RHINO.evaluateURL(testContext, url, testScope);
		} catch (Exception e) {
			if (e instanceof WrappedException) {
				WrappedException ee = (WrappedException)e;
				//TODO
				String s = ee.getScriptStackTrace(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return true;
					}
				});
				System.err.println(s);
				Throwable t = ee.getWrappedException();
				if (t instanceof Error) {
					throw (Error)t;
				}
				throw (Exception)t;
			}
			throw e;
		}
	}
	
	@Test
	public void testRunAll() throws Exception {
		String[] scriptLocations = getScriptUrls();
		if (scriptLocations == null) {
			scriptLocations = new String[] {
				"classpath:" + getClass().getPackage().getName().replace('.', '/')
				+ "/" + getClass().getSimpleName() + ".js"
			};
		}
		for (String location : scriptLocations) {
			runJSTestCase(new URL(location));
		}
	}
}