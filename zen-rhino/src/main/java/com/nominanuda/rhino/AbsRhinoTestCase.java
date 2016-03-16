package com.nominanuda.rhino;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.mozilla.javascript.DebuggableRhinoEmbedding;
import org.mozilla.javascript.MethodArgCoercer;
import org.mozilla.javascript.PluggableMethodArgCoercer;
import org.mozilla.javascript.PluggableWrapFactory;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.WrapFactory;

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

public abstract class AbsRhinoTestCase {
	
	
	/* setup */
	
	@Before
	public void setup() throws Exception {
		new PluggableURLStreamHandlerFactory().installToJvm();
	}
	
	protected RhinoEmbedding buildRhinoEmbedding() {
		RhinoEmbedding embedding = new DebuggableRhinoEmbedding();
		embedding.setWrapFactory(getWrapFactory(embedding));
		embedding.setMethodArgCoercer(getMethodArgCoercer());
		embedding.setDebug(isDebug());
		embedding.init();
		return embedding;
	}
	
	protected Require buildRhinoRequire() {
		ModuleRegistry moduleRegistry = new ModuleRegistry();
		moduleRegistry.setModuleFactories(buildModuleFactories());
		Require require = new Require();
		require.setRegistry(moduleRegistry);
		return require;
	}

	protected WrapFactory getWrapFactory(RhinoEmbedding rhinoEmbedding) {
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
	
	protected boolean isDebug() {
		return false;
	}
}
