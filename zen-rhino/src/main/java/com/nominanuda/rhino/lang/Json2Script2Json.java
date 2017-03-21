package com.nominanuda.rhino.lang;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.mozilla.javascript.RhinoEmbedding;

import com.nominanuda.rhino.IScriptSource;
import com.nominanuda.rhino.IScriptSource.IScript;
import com.nominanuda.rhino.ObjectFactory;
import com.nominanuda.rhino.SimpleScriptSource;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.stereotype.Disposable;

public class Json2Script2Json implements Function<Stru, Stru>, Disposable {
	
	public static class Factory implements ObjectFactory<Json2Script2Json> {
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
		public Json2Script2Json getObject() {
			try {
				return new Json2Script2Json(scriptSource);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("could not istantiate scriptSource, error: " + e.getMessage());
			}
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