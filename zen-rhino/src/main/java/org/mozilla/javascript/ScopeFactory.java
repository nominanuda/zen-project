package org.mozilla.javascript;


import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.stereotype.Factory;

/**
 * this class is supposed to be immutable by other parts of the framework !! beware
 *
 */
public class ScopeFactory implements Factory<Scriptable> {
	private final static int FIXED_ROOT = ScriptableObject.DONTENUM | ScriptableObject.READONLY;
	private ScriptableObject cachedScope = null;
	private RhinoEmbedding embedding;

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || ! (obj instanceof ScopeFactory)) {
			return false;
		}
		return toString().equals(obj.toString());
	}
	private final String toStringVal;
	@Override
	public String toString() {
		return toStringVal;
	}

	private List<String> sourceScripts = Collections.emptyList();
	private List<Class<? extends Script>> compiledScripts = Collections.emptyList();
	private List<Class<? extends Scriptable>> definedClasses = Collections.emptyList();
	private Map<String, Object> javaObjects = Collections.emptyMap();
	private boolean sealed = false;
	private boolean immutable = false;
	private boolean allowJavaPackageAccess = false;

	public ScopeFactory(List<String> _sourceScripts,
							List<Class<? extends Script>> _compiledScripts,
							List<Class<? extends Scriptable>> _definedClasses, 
							Map<String, Object> _javaObjects,
							boolean _sealed) {
		immutable = true;
		if(_sourceScripts != null) sourceScripts = _sourceScripts;
		if(_compiledScripts != null) compiledScripts = _compiledScripts;
		if(_definedClasses != null) definedClasses = _definedClasses;
		if(_javaObjects != null) javaObjects = _javaObjects;
		sealed = _sealed;
		toStringVal = 
			new Boolean(sealed).toString()
			+ sourceScripts.toString()
			+ compiledScripts.toString()
			+ definedClasses.toString()
			+ javaObjects.toString();
	}
	public ScopeFactory() {
		toStringVal = toString();
	}
	
	public boolean isSealed() {
		return sealed;
	}

	public List<String> getSourceScripts() {
		return sourceScripts;
	}

	public List<Class<? extends Script>> getCompiledScripts() {
		return compiledScripts;
	}

	public List<Class<? extends Scriptable>> getDefinedClasses() {
		return definedClasses;
	}

	public Map<String, Object> getJavaObjects() {
		return javaObjects;
	}

	@Override
	public Scriptable get() throws RuntimeException {
		return create();
	}
	public Scriptable create() throws RuntimeException {
		Context cx = Context.getCurrentContext();
		if(cx == null) {
			try {
				cx = embedding != null
					? cx = embedding.enterContext()
					: Context.enter();
				return createInContext(cx);
			} finally {
				Context.exit();
			}
		} else {
			return createInContext(cx);
		}
	}
	public Scriptable createInCurrentContext() throws RuntimeException {
		return createInContext(Context.getCurrentContext());
	}
	public Scriptable createInContext(Context cx) throws RuntimeException {
		if (cachedScope != null) {
			return this.isSealed() ? cachedScope : RHINO.protocloneScriptable(cx, cachedScope);
		} else {
			ScriptableObject s;
			try {
				s = RHINO.createTopScope(cx, allowJavaPackageAccess);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			for (String k : this.getJavaObjects().keySet()) {
				s.defineProperty(k, this.getJavaObjects().get(k), FIXED_ROOT);
			}
			for (Class<? extends Scriptable> cl : this.getDefinedClasses()) {
				RHINO.defineClassInScope(s, cl);
//				TODO remove me
//				if(MixedHostObject.class.isAssignableFrom(cl)){
//					handleMixedHostObject(cl, s, cx);
//				}
			}
			for (Class<? extends Script> cl : this.getCompiledScripts()) {
				Script script = RHINO.instantiate(cl, cx);
				RHINO.evaluateScript(script, cx, s);
			}
			for (String path : this.getSourceScripts()) {
				Reader r;
				try {
					r = new InputStreamReader(new URL(path).openStream());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				RHINO.evaluateReader(cx, r, path, s);
			}
			if (this.isSealed()) {
				s.sealObject();
			}
			cachedScope = s;
			return this.isSealed() ? s : RHINO.protocloneScriptable(cx, s);
		}
	}

	public void setAllowJavaPackageAccess(boolean allowJavaPackageAccess) {
		this.allowJavaPackageAccess = allowJavaPackageAccess;
		cachedScope = null; // reset;
	}

	public void setSourceScripts(List<String> sourceScripts) {
		Check.illegalstate.assertFalse(immutable);
		this.sourceScripts = sourceScripts;
		cachedScope = null; // reset;
	}

	public void setCompiledScripts(List<Class<? extends Script>> compiledScripts) {
		Check.illegalstate.assertFalse(immutable);
		this.compiledScripts = compiledScripts;
		cachedScope = null; // reset;
	}

	public void setDefinedClasses(List<Class<? extends Scriptable>> definedClasses) {
		Check.illegalstate.assertFalse(immutable);
		this.definedClasses = definedClasses;
		cachedScope = null; // reset;
	}

	public void setJavaObjects(Map<String, Object> javaObjects) {
		Check.illegalstate.assertFalse(immutable);
		this.javaObjects = javaObjects;
		cachedScope = null; // reset;
	}

	public void setSealed(boolean sealed) {
		Check.illegalstate.assertFalse(immutable);
		this.sealed = sealed;
	}

	public void setEmbedding(RhinoEmbedding embedding) {
		this.embedding = embedding;
	}

	public boolean isAllowJavaPackageAccess() {
		return allowJavaPackageAccess;
	}
}
