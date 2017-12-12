package com.nominanuda.rhino;

import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.nominanuda.zen.obj.JsonDeserializer;

import com.nominanuda.rhino.host.SourceModuleFactory;

public abstract class AbsScriptSource implements IScriptSource {
	private final static SourceModuleFactory MODS = new SourceModuleFactory();
	
	private final Map<String, Object> hostObjs = new HashMap<String, Object>();
	private String jsSuffix = ".js", jsonSuffix = ".json";
	private RhinoEmbedding rhinoEmbedding;
	private ScriptableObject cachedScope;
	
	public abstract String source(boolean doReset) throws IOException;
	protected abstract Script script(Context cx, String source, boolean doSave);
	
	@Override
	public void setHostObject(String key, Object obj) {
		hostObjs.put(key, obj);
	}
	
	@Override
	public IScript open(String source, boolean doSave) throws Exception {
		final Context cx = rhinoEmbedding.enterContext();
		if (cachedScope == null) {
			cachedScope = RHINO.createTopScope(cx, false);
			for (Entry<String, Object> hostObj : hostObjs.entrySet()) {
				cachedScope.defineProperty(hostObj.getKey(), resolveHostObject(hostObj.getValue(), cx, cachedScope), ScriptableObject.DONTENUM | ScriptableObject.READONLY);
			}
		}
		Scriptable ctrlScope = RHINO.protocloneScriptable(cx, cachedScope);
		RHINO.evaluateScript(script(cx, source, doSave), cx, ctrlScope);
		return new ScriptWrapper(cx, ctrlScope, source(false));
	}
	
	@Override
	public IScript open(String source) throws Exception {
		return open(source, false);
	}
	
	@Override
	public IScript open() throws Exception {
		return open(null, false);
	}
	
	@Override
	public IScript reset() throws Exception {
		source(true);
		return open();
	}
	
	
	/* host objs smartness */
	
	private Object resolveHostObject(Object value, Context cx, Scriptable scope) throws Exception {
		if (value instanceof List) { // list -> array
			return RHINO.fromList((List<?>) value, cx, scope);
		}
		if (value instanceof Map) { // map -> object
			return RHINO.fromMap((Map<?, ?>) value, cx, scope);
		}
		if (value instanceof String) {
			String string = (String) value;
			if (string.endsWith(jsSuffix)) { // .js source
				return MODS.create(string, null, scope, cx);
			}
			if (string.endsWith(jsonSuffix)) { // .json structure
				return JsonDeserializer.JSON_DESERIALIZER.deserialize((new URL(string).openStream()));
			}
		}
		return value;
	}
	
	
	/* setters */
	
	public void setRhinoEmbedding(RhinoEmbedding rhinoEmbedding) {
		this.rhinoEmbedding = rhinoEmbedding;
	}
	
	public void setHostObjects(Map<String, Object> objs) {
		if (objs != null) {
			for (Entry<String, Object> entry: objs.entrySet()) {
				setHostObject(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void setJsSuffix(String suffix) {
		jsSuffix = suffix;
	}
	
	public void setJsonSuffix(String suffix) {
		jsonSuffix = suffix;
	}
}
