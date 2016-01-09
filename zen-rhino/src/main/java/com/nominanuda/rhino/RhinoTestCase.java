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

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.DebuggableRhinoEmbedding;
import org.mozilla.javascript.MethodArgCoercer;
import org.mozilla.javascript.PluggableMethodArgCoercer;
import org.mozilla.javascript.PluggableWrapFactory;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.RhinoHelper;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.WrappedException;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
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
		(rhinoEmbedding = new DebuggableRhinoEmbedding()).setDebug(isDebug());
		rhinoEmbedding.setWrapFactory(getWrapFactory());
		rhinoEmbedding.setMethodArgCoercer(getMethodArgCoercer());
		rhinoEmbedding.init();
		testContext = rhinoEmbedding.enterContext();
		testScope = rhino.protocloneScriptable(testContext, rhino.createTopScope(testContext, true));
		ModuleRegistry moduleRegistry = new ModuleRegistry();
		moduleRegistry.setModuleFactories(buildModuleFactories());
		Require require = new Require();
		require.setRegistry(moduleRegistry);
		rhino.putProperty(testScope, "require", require);
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
			return rhino.evaluateURL(testContext, url, testScope);
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
	
	
	/* setup */

	protected boolean isDebug() {
		return false;
	}
	
	protected WrapFactory getWrapFactory() {
		PluggableWrapFactory wf = new PluggableWrapFactory(rhinoEmbedding);
		wf.setConvertors(Arrays.asList(new DataStructConvertor()));
		return wf;
	}
	
	protected MethodArgCoercer getMethodArgCoercer() {
		return new PluggableMethodArgCoercer(Collections.buildMap(LinkedHashMap.class,
			String.class, new Tuple2<ObjectConvertor<?, ?, ?>, Integer>(new UndefinedCoercer(), 1),
			DataArray.class, new Tuple2<ObjectConvertor<?, ?, ?>, Integer>(new ToDataArrayCoercer(), 1),
			DataObject.class, new Tuple2<ObjectConvertor<?, ?, ?>, Integer>(new ToDataObjectCoercer(), 1),
			DataStruct.class, new Tuple2<ObjectConvertor<?, ?, ?>, Integer>(new ToDataStructCoercer(), 1),
			Object.class, new Tuple2<ObjectConvertor<?, ?, ?>, Integer>(new ToDataStructCoercer(), 2)));
	}
	
	protected List<? extends ModuleFactory> buildModuleFactories() {
		List<ModuleFactory> l = new LinkedList<ModuleFactory>();
		l.add(new SourceModuleFactory());
		HostObjectFactory mf = new HostObjectFactory();
		mf.addObject("console", "com.nominanuda.rhino.host.Console");
		l.add(mf);
		l.add(new JavaObjectFactory(buildJavaObjectsMap()));
		return l;
	}
	
	protected Map<String, Object> buildJavaObjectsMap() {
		return new HashMap<String, Object>();
	}

	protected @Nullable String[] getScriptUrls() {
		return null;
	}
}