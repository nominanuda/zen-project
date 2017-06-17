package com.nominanuda.rhino;

import static com.nominanuda.zen.seq.Seq.SEQ;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.junit.Before;
import org.mozilla.javascript.DebuggableRhinoEmbedding;
import org.mozilla.javascript.MethodArgCoercer;
import org.mozilla.javascript.PluggableMethodArgCoercer;
import org.mozilla.javascript.PluggableWrapFactory;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.WrapFactory;

import com.nominanuda.rhino.host.HostObjectFactory;
import com.nominanuda.rhino.host.JavaObjectFactory;
import com.nominanuda.rhino.host.ModuleFactory;
import com.nominanuda.rhino.host.ModuleRegistry;
import com.nominanuda.rhino.host.Require;
import com.nominanuda.rhino.host.SourceModuleFactory;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.jvmurl.PluggableURLStreamHandlerFactory;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

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
	
	protected WrapFactory getWrapFactory(RhinoEmbedding rhinoEmbedding) {
		PluggableWrapFactory wf = new PluggableWrapFactory(rhinoEmbedding);
		wf.setConvertors(Arrays.asList(new StruConvertor()));
		return wf;
	}
	
	protected MethodArgCoercer getMethodArgCoercer() {
		return new PluggableMethodArgCoercer(SEQ.buildMap(LinkedHashMap.class,
			String.class, new Tuple2<ObjectCoercer<?, ?, ?>, Integer>(new UndefinedCoercer(), 1),
			Arr.class, new Tuple2<ObjectCoercer<?, ?, ?>, Integer>(new ToArrCoercer(), 1),
			Obj.class, new Tuple2<ObjectCoercer<?, ?, ?>, Integer>(new ToObjCoercer(), 1),
			Stru.class, new Tuple2<ObjectCoercer<?, ?, ?>, Integer>(new ToStruCoercer(), 1),
			Object.class, new Tuple2<ObjectCoercer<?, ?, ?>, Integer>(new ToStruCoercer(), 2)));
	}
	
	
	protected Require buildRhinoRequire() {
		ModuleRegistry moduleRegistry = new ModuleRegistry();
		moduleRegistry.setModuleFactories(buildModuleFactories());
		Require require = new Require();
		require.setRegistry(moduleRegistry);
		return require;
	}
	
	protected List<? extends ModuleFactory> buildModuleFactories() {
		List<ModuleFactory> l = new LinkedList<ModuleFactory>();
		l.add(new SourceModuleFactory());
		HostObjectFactory hof = new HostObjectFactory();
		hof.addObject("console", "com.nominanuda.rhino.host.Console");
		l.add(hof);
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
