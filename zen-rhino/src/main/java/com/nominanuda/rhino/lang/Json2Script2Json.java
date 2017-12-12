package com.nominanuda.rhino.lang;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.mozilla.javascript.RhinoEmbedding;

import com.nominanuda.rhino.IScriptSource;
import com.nominanuda.rhino.IScriptSource.IScript;
import com.nominanuda.rhino.SimpleScriptSource;
import com.nominanuda.rhino.host.Console;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.stereotype.Disposable;
import com.nominanuda.zen.stereotype.Reloadable;

public class Json2Script2Json implements Function<Stru, Stru>, Disposable {
	
	public static class Factory implements com.nominanuda.zen.stereotype.Factory<Json2Script2Json>, Reloadable {
		private final IScriptSource scriptSource;
		
		public Factory(IScriptSource scriptSource) {
			this.scriptSource = scriptSource;
		}
		public Factory(String file, Map<String, Object> objs, RhinoEmbedding embedding) throws IOException {
			SimpleScriptSource scriptSource = new SimpleScriptSource(file);
			scriptSource.setRhinoEmbedding(embedding);
			scriptSource.setHostObjects(objs);
			this.scriptSource = scriptSource;
		}
		public Factory(String file, RhinoEmbedding embedding) throws IOException {
			this(file, null, embedding);
		}
		
		@Override
		public Json2Script2Json get() {
			try {
				return new Json2Script2Json(scriptSource);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("could not istantiate scriptSource, error: " + e.getMessage());
			}
		}
		
		@Override
		public void reload() throws Exception {
			scriptSource.reset();
		}
		
		public void setEnableConsoleWithKey(String consoleKey) {
			scriptSource.setHostObject(consoleKey, Console.asJavaHostObj());
		}
		
		public void setEnableConsole(boolean enable) {
			if (enable) setEnableConsoleWithKey(Console.HOSTOBJ_KEY);
		}
	}
	
	
	private final IScript script;
	private String function = "json2json";
	
	public Json2Script2Json(IScriptSource scriptSource) throws Exception {
		script = scriptSource.open();
	}
	public Json2Script2Json(String file, Map<String, Object> objs, RhinoEmbedding embedding) throws Exception {
		SimpleScriptSource scriptSource = new SimpleScriptSource(file);
		scriptSource.setRhinoEmbedding(embedding);
		scriptSource.setHostObjects(objs);
		script = scriptSource.open();
	}
	public Json2Script2Json(String file, RhinoEmbedding embedding) throws Exception {
		this(file, null, embedding);
	}

	@Override
	public Stru apply(Stru json) {
		return script.callForDataStruct(function, json);
	}
	
	@Override
	public void dispose() {
		script.close();
	}
	
	
	/* setters */
	
	public void setFunction(String function) {
		this.function = function;
	}
}