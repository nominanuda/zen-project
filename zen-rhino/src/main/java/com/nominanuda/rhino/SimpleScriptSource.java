package com.nominanuda.rhino;


import static com.nominanuda.zen.common.Str.STR;
import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;


public class SimpleScriptSource extends AbsScriptSource {
	private final String file;
	private String source;
	private Script script;
	
	public SimpleScriptSource(String file) throws IOException {
		this.file = file;
		source(true);
	}
	
	@Override
	public String source(boolean doReset) throws IOException {
		if (doReset) {
			source = IO.readAndCloseUtf8(new URL(file).openStream());
			script = null;
		}
		return source;
	}
	
	@Override
	protected Script script(Context cx, String source, boolean doSave) {
		if (STR.nullOrBlank(source)) {
			if (script != null) {
				return script;
			}
			source = this.source;
			doSave = true;
		}
		Script script = cx.compileString(source, file, 1, null);
		if (doSave) {
			this.source = source;
			this.script = script;
		}
		return script;
	}
}
