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
package org.mozilla.javascript;

import static com.nominanuda.io.IOHelper.IO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mozilla.javascript.tools.ToolErrorReporter;

import com.nominanuda.code.Nullable;
import com.nominanuda.code.ThreadSafe;
import com.nominanuda.lang.Check;

@ThreadSafe
public class RhinoHelper {
	public static final RhinoHelper RHINO = new RhinoHelper();
	private final static String[] TOP_JAVA_NAMES = { 
		"Packages", "java", "javax", "org", "com", "edu", "net" };
	protected Object securityDomain;

	public ScriptableObject protocloneScriptable(Context cx, Scriptable scope) {
		ScriptableObject res = new NativeObject();
		res.setPrototype(scope);
		res.setParentScope(null);
		return res;
	}
	public ScriptableObject createTopScope(Context cx, boolean allowJavaPackageAccess) throws Exception {//TODO more fine grained IOEx + some other
		ScriptableObject root;
		if(allowJavaPackageAccess) {
			root = new ImporterTopLevel(cx);
		} else {
			root =  new NativeObject();
			ScriptRuntime.initStandardObjects(cx, root, false);
			disallowJavaPackagesAccess(root);
		}
		return root;
	}
	private void disallowJavaPackagesAccess(ScriptableObject root) {
		for(String topJavaName : TOP_JAVA_NAMES) {
			ScriptableObject.deleteProperty(root, topJavaName);
		}
	}

	public void defineClassInScope(Scriptable scope, Class<? extends Scriptable> clazz) throws UndeclaredThrowableException {
		try {
			ScriptableObject.defineClass(scope, clazz);
		} catch (IllegalAccessException e) {
			throw new UndeclaredThrowableException(e);
		} catch (InstantiationException e) {
			throw new UndeclaredThrowableException(e);
		} catch (InvocationTargetException e) {
			throw new UndeclaredThrowableException(e);
		}
	}

	public Script compileScript(Reader r, String fileName, Object securityDomain, Context cx)
			throws EvaluatorException {
		try {
			return cx.compileReader(r, fileName, 1, securityDomain);
		} catch (EvaluatorException e) {
			throw Context.reportRuntimeError(e.getMessage());
		} catch (IOException e) {
			throw Context.reportRuntimeError(e.getMessage());
		} finally {
			try {
				r.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		/* catch (RhinoException e) {
			ToolErrorReporter.reportException(cx.getErrorReporter(), e);
			throw Context.reportRuntimeError(e.getMessage());
		} catch (VirtualMachineError ex) {
			String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ex.toString());
			throw Context.reportRuntimeError(msg);
		}*/
	}

	public Object evaluateScript(Script script, Context cx, Scriptable scope) throws EvaluatorException {
		try {
			return script.exec(cx, scope);
		} catch (RhinoException e) {
			throw e;//TODO new WrappedRhinoException(e);
		} catch (VirtualMachineError e) {
			String msg = ToolErrorReporter.getMessage(
					"msg.uncaughtJSException", e.getMessage());
			throw Context.reportRuntimeError(msg);
		}
	}

	public Object evaluateScriptWithContinuations(Script script, Context cx, Scriptable scope) throws EvaluatorException {
		try {
			cx.setOptimizationLevel(-1);
			return cx.executeScriptWithContinuations(script, scope);
		} catch (RhinoException e) {
			throw e;//TODO new WrappedRhinoException(e);
		} catch (VirtualMachineError e) {
			String msg = ToolErrorReporter.getMessage(
					"msg.uncaughtJSException", e.getMessage());
			throw Context.reportRuntimeError(msg);
		}
	}

	//TODO
	public Script instantiate(Class<? extends Script> clazz, Context cx) throws EvaluatorException {
		try {
			GeneratedClassLoader loader = SecurityController.createLoader(
					cx.getApplicationClassLoader(), securityDomain);
			loader.linkClass(clazz);
			if (!Script.class.isAssignableFrom(clazz)) {
				throw Context.reportRuntimeError("msg.must.implement.Script");
			}
			return clazz.newInstance();
		} catch (InstantiationException e) {
			Context.reportError(e.getMessage());
			throw Context.reportRuntimeError(e.getMessage());
		} catch (IllegalAccessException e) {
			Context.reportError(e.getMessage());
			throw Context.reportRuntimeError(e.getMessage());
		}
		/*} catch (RhinoException e) {
			ToolErrorReporter.reportException(cx.getErrorReporter(), e);
			throw Context.reportRuntimeError(e.getMessage());
		*/
	}

	protected Object getSecurityDomain() {
		return securityDomain;
	}

	public void setSecurityDomain(Object securityDomain) {
		this.securityDomain = securityDomain;
	}

	public Object evaluateURL(Context cx, URL url, Scriptable scope) throws IOException {
		Reader r = new InputStreamReader(url.openStream(), "UTF-8");///TODO detect charset if possible
		return evaluateReader(cx, r, url.toString(), scope);
	}
	public Object evaluateReader(Context cx, Reader src, String path,
			Scriptable scope) {
		//TODO should throw exceptions..
		boolean allowContinuations = false;
//			try {
//				URI u = new URI(scriptName);
//				if(u.isAbsolute()) {
//					scope.put(INCUDE_BASE, scope, u);
//				}
//			} catch(Exception e) {}
			Script script = compileScript(src, path, /*TODO*/null, cx);
			return allowContinuations
				? evaluateScriptWithContinuations(script, cx, scope)
				: evaluateScript(script, cx, scope);
	}

	public Scriptable newObject(Context cx, Scriptable contructorSearchScope) {
		return cx.newObject(contructorSearchScope);
	}
	public Scriptable newArray(Context cx, Scriptable contructorSearchScope) {
		return cx.newArray(contructorSearchScope, 0);
	}
	public boolean isArray(Object o) {
		return ScriptRuntime.isArrayObject(o);
	}
	

	//TODO WRONG !!!!!!! call (Callable) e basta!!
	public Object callFunctionInScope(Context cx, Scriptable scope, String functionName, Object[] args) {
		return callFunctionInScope(cx, scope, functionName, args, false);
	}

	public Object callFunctionInScopeWithContinuations(Context cx, Scriptable scope, String functionName, Object[] args)
			throws ContinuationPending {
		return callFunctionInScope(cx, scope, functionName, args, true);
	}

	protected Object callFunctionInScope(Context cx, Scriptable scope, String functionName, Object[] args, boolean allowContinuations) {
		if(args == null) {
			args = new Object[0];
		}
		Object f = scope.get(functionName, scope);
		if (f == null || !(f instanceof Function)) {
			throw new IllegalArgumentException("Cannot find function " + functionName+" in provided scope");
		}
		Function fun = (Function)f;
		Object res = allowContinuations
			? cx.callFunctionWithContinuations(fun, scope, args)
			: fun.call(cx, scope, scope, args);
		return res;
	}

	public Map<String, Function> findFunctionsInScope(Scriptable scope) {
		Map<String, Function> res = new LinkedHashMap<String, Function>();
		Object[] ids = scope.getIds();
		for(Object id : ids) {
			if(id instanceof String) {
				Object var = scope.get((String)id, scope);
				if (var != null && var instanceof Function) {
					res.put((String)id, (Function)var);
				}
			}
		}
		return res;
	}
	public @Nullable Function findFunctionInScope(Scriptable scope, String name) {
		Object[] ids = scope.getIds();
		for(Object id : ids) {
			if(id.equals(name)) {
				Object var = scope.get((String)id, scope);
				if (var != null && var instanceof Function) {
					return (Function)var;
				}
			}
		}
		return null;
	}

	public Scriptable jsonToScriptable(Context cx, Reader json) {
		Reader r = IO.concat(
			new StringReader("(function(){return "),
			json, new StringReader("})();"));
		return (Scriptable)evaluateReader(cx, r, " - ", scopeFactory.createInContext(cx));
	}
	
	private ScopeFactory scopeFactory = new ScopeFactory();
	
//	public Object[] nativeArrayToJavaArray(NativeArray arr) {
//		List<Object> l = new LinkedList<Object>();
//		for(int i = 0, len=(int)arr.getLength(); i < len; i++) {
//			l.add(i, arr.get(i, arr));
//		}
//		return l.toArray(new Object[l.size()]);
//	}
//	public void putOrAppendProp(Scriptable obj, String pName, Object pVal) {
//		if(obj.has(pName, obj)) {
//			Object val = obj.get(pName, obj);
//			if(isArray(val)) {
//				NativeArray arr = (NativeArray)val;
//				int last = (int)arr.getLength();
//				arr.put(last, arr, pVal);
//			} else {
//				Scriptable arr = newArray(obj);
//				arr.put(0, arr, val);
//				arr.put(1, arr, pVal);
//				obj.put(pName, obj, arr);
//			}
//		} else {
//			obj.put(pName, obj, pVal);
//		}
//	}
	
	public Object getProperty(Scriptable obj, Object index) {
		index = validateIndex(index);
		return index instanceof String
			? ScriptableObject.getProperty(obj, (String)index)
			: ScriptableObject.getProperty(obj, ((Number)index).intValue());
	}
	public Object deleteProperty(Scriptable obj, Object index) {
		index = validateIndex(index);
		return index instanceof String
			? ScriptableObject.deleteProperty(obj, (String)index)
			: ScriptableObject.deleteProperty(obj, ((Number)index).intValue());
	}
	public void putProperty(Scriptable obj, Object index, Object value) {
		index = validateIndex(index);
		if (index instanceof String) {
			ScriptableObject.putProperty(obj, (String)index, value);
		} else {
			ScriptableObject.putProperty(obj, ((Number)index).intValue(), value);
		}
	}
	private Object validateIndex(Object index) {
		Check.notNull(index);
		if (index instanceof String) {
			final String s = (String)index;
			final int l = s.length();
			if (l > 0) {
				for (int i = 0; i < l; i++) {
					char c = s.charAt(i);
					if (c < '0' || c > '9') {
						return s;
					}
				}
				return Integer.parseInt(s);
			}
		}
		return index;
	}
	
	public <T extends Scriptable> BaseFunction buildClassCtor(Scriptable scope, Class<T> clazz,boolean sealed,boolean mapInheritance) throws IllegalAccessException, InstantiationException,InvocationTargetException{
		return ScriptableObject.buildClassCtor(scope, clazz, sealed, mapInheritance);
	}
	
	public boolean isUndefined(Object val) {
		return Undefined.instance == val;
	}
}
