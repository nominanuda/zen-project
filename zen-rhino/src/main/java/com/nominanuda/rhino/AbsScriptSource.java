package com.nominanuda.rhino;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static com.nominanuda.io.IOHelper.IO;
import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoEmbedding;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class AbsScriptSource implements IScriptSource {
	private ScriptableObject cachedScope;
	private RhinoEmbedding rhinoEmbedding;
	private String jsSuffix = ".js", jsonSuffix = ".json";
	private final Map<String, Object> hostObjs = new HashMap<String, Object>();
	
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
		if (value instanceof String) {
			String string = (String) value;
			if (string.endsWith(jsSuffix)) {
				Scriptable moduleScope = RHINO.newObject(cx, scope);
				RHINO.putProperty(moduleScope, "exports", RHINO.newObject(cx, moduleScope));
				RHINO.evaluateURL(cx, new URL(string), moduleScope);
				return RHINO.getProperty(moduleScope, "exports");
			}
			if (string.endsWith(jsonSuffix)) {
				return STRUCT.parse(IO.readAndCloseUtf8(new URL(string).openStream()), false);
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
