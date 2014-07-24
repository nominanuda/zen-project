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

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.PluggableMethodArgCoercer;
import org.mozilla.javascript.PluggableWrapFactory;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Collections;
import com.nominanuda.lang.ObjectConvertor;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.rhino.host.HostObjectFactory;
import com.nominanuda.rhino.host.JavaObjectFactory;
import com.nominanuda.rhino.host.ModuleFactory;
import com.nominanuda.rhino.host.ModuleRegistry;
import com.nominanuda.rhino.host.Require;
import com.nominanuda.rhino.host.SourceModuleFactory;
import com.nominanuda.uri.PluggableURLStreamHandlerFactory;


public abstract class RhinoTestCase {
	protected RhinoHelper rhino = new RhinoHelper();
	protected RhinoEmbedding rhinoEmbedding;
	protected ScriptableObject testScope;
	protected Context testContext;
	@Before
	public void setUp() throws Exception {
		new PluggableURLStreamHandlerFactory().installToJvm();
		rhinoEmbedding = new RhinoEmbedding();
		rhinoEmbedding.setDebug(isDebug());
		Map<Class<?>, Tuple2<ObjectConvertor<Object, Object, Exception>,Integer>> coerceTypeMap = 
			Collections.buildMap(LinkedHashMap.class, DataStruct.class, new Tuple2<ObjectConvertor<?, ?, ?>,Integer>(
					new ToDataStructCoercer(), 1));
		PluggableWrapFactory wf = new PluggableWrapFactory(rhinoEmbedding);
		wf.setConvertors(Arrays.asList(new DataStructConvertor()));
		rhinoEmbedding.setWrapFactory(wf);
		PluggableMethodArgCoercer coercer = new PluggableMethodArgCoercer();
		coercer.setConvertors(coerceTypeMap);
		rhinoEmbedding.setMethodArgCoercer(coercer);
		rhinoEmbedding.init();
		testContext = rhinoEmbedding.enterContext();
		testScope = rhino.protocloneScriptable(testContext, 
				rhino.createTopScope(testContext, true));
		ModuleRegistry moduleRegistry = new ModuleRegistry();
		List<? extends ModuleFactory> l = buildModuleFactories();
		moduleRegistry.setModuleFactories(l);
		Require require = new Require();
		require.setRegistry(moduleRegistry);
		rhino.putProperty(testScope, "require", require);
		//URL url = new URL("classpath:/com/nominanuda/rhino/JUnit.js");
		//rhino.evaluateURL(testContext, url, testScope);
		onSetup();
	}

	protected List<? extends ModuleFactory> buildModuleFactories() {
		List<ModuleFactory> l = new LinkedList<ModuleFactory>();
		l.add(new SourceModuleFactory());
		HostObjectFactory mf = new HostObjectFactory();
		mf.addObject("console", "com.nominanuda.rhino.host.Console");
		l.add(mf);
		Map<String, Object> m = java.util.Collections.emptyMap();
		JavaObjectFactory jof = new JavaObjectFactory(m);
		l.add(jof);
		return l;
	}
	protected void onSetup() throws Exception {
	}
	@After
	public void tearDown() throws Exception {
		Context.exit();
	}
	protected Object runJSTestCase(URL url) throws Exception {
		try {
			Object res = rhino.evaluateURL(testContext, url, testScope);
			return res;
		} catch (Exception e) {
			if(e instanceof WrappedException) {
				WrappedException ee = (WrappedException)e;
				//TODO
				String s = ee.getScriptStackTrace();
				System.err.println(s);
				Throwable t = ee.getWrappedException();
				if(t instanceof Error) {
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
		if(scriptLocations == null) {
			scriptLocations = new String[] {
				"classpath:/"
				+getClass().getPackage().getName().replace('.', '/')
				+"/"+getClass().getSimpleName()+".js"
			};
		}
		for(String location : scriptLocations) {
			runJSTestCase(new URL(location));
		}
	}

	protected @Nullable String[] getScriptUrls() {
		return null;
	}
	protected boolean isDebug() {
		return false;
	}
}
