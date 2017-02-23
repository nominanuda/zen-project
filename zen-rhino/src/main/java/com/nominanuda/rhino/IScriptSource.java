package com.nominanuda.rhino;

import java.io.IOException;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public interface IScriptSource {
	public interface IScript extends AutoCloseable {
		public void close();
		Object call(String function, Object... args);
		Arr callForDataArray(String function, Object... args);
		Obj callForDataObject(String function, Object... args);
		Stru callForDataStruct(String function, Object... args);
		String source();
	}
	
	void setHostObject(String key, Object obj);
	
	String source(boolean doReset) throws IOException;
	
	IScript open(String source, boolean doSave) throws Exception;
	IScript open(String source) throws Exception;
	IScript open() throws Exception;
	
	IScript reset() throws Exception;
}
