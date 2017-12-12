/*
 * AZ: this version facilitates spring instantiation
 */

package org.mozilla.javascript;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nominanuda.rhino.host.Console;
import com.nominanuda.rhino.host.HostObjectFactory;
import com.nominanuda.rhino.host.JavaObjectFactory;
import com.nominanuda.rhino.host.ModuleFactory;
import com.nominanuda.rhino.host.ModuleRegistry;
import com.nominanuda.rhino.host.Require;
import com.nominanuda.rhino.host.SourceModuleFactory;

public class SpringScopeFactory extends ScopeFactory {
	private RhinoEmbedding embedding;
	private Map<String, Object> requireJavaObjs;
	private boolean cache = true;
	
	public RhinoEmbedding getEmbedding() { // for RhinoHandler
		return embedding;
	}
	
	public boolean getCache() { // for RhinoHandler
		return cache;
	}
	
	
	public SpringScopeFactory() {
		setJavaObjects(new LinkedHashMap<String, Object>()); // override ScopeFactory's immutable empty map
	}
	
	
	public void init() {
		Map<String, Object> javaObjs = getJavaObjects();
		if (javaObjs.isEmpty() || requireJavaObjs != null) {
			List<ModuleFactory> factories = new LinkedList<>();
			factories.add(new SourceModuleFactory());
			HostObjectFactory hoFactory = new HostObjectFactory();
			hoFactory.addObject(Console.HOSTOBJ_KEY, "com.nominanuda.rhino.host.Console");
			factories.add(hoFactory);
			if (requireJavaObjs != null) {
				factories.add(new JavaObjectFactory(requireJavaObjs));
			}
			ModuleRegistry moduleRegistry = new ModuleRegistry();
			moduleRegistry.setModuleFactories(factories);
			moduleRegistry.setCache(getCache());
			Require require = new Require();
			require.setRegistry(moduleRegistry);
			javaObjs.put("require", require);
			setJavaObjects(javaObjs);
		}
	}
	
	
	/* setters */

	@Override
	public void setEmbedding(RhinoEmbedding embedding) {
		this.embedding = embedding;
		super.setEmbedding(embedding);
	}
	
	/**
	 * Call this method to have the scope automatically filled with a require
	 * module capable of loading those java objects by their map name
	 * @param requireJavaObjs
	 */
	public void setRequireJavaObjects(Map<String, Object> requireJavaObjs) {
		this.requireJavaObjs = requireJavaObjs;
	}
	
	public void setRequireJavaObjectsMaps(List<Map<String, Object>> requireJavaObjsMaps) {
		requireJavaObjs = new LinkedHashMap<String, Object>();
		for (Map<String, Object> map : requireJavaObjsMaps) {
			requireJavaObjs.putAll(map);
		}
	}
	
	public void setCache(boolean cache) {
		this.cache = cache;
	}
}
