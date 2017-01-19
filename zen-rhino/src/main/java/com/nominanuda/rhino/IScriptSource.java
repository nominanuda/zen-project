package com.nominanuda.rhino;

import java.io.IOException;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;

public interface IScriptSource {
	public interface IScript extends AutoCloseable {
		public void close();
		Object call(String function, Object... args);
		DataArray callForDataArray(String function, Object... args);
		DataObject callForDataObject(String function, Object... args);
		DataStruct callForDataStruct(String function, Object... args);
		String source();
	}
	
	void setHostObject(String key, Object obj);
	
	String source(boolean doReset) throws IOException;
	
	IScript open(String source, boolean doSave) throws Exception;
	IScript open(String source) throws Exception;
	IScript open() throws Exception;
	
	IScript reset() throws Exception;
}
