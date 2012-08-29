package com.nominanuda.dataobject.transform;

import com.nominanuda.dataobject.JsonContentHandler;

public class BaseJsonTransformer implements JsonTransformer {
	private JsonContentHandler target;

	public void setTarget(JsonContentHandler target) {
		this.target = target;
	}
	public void startJSON() throws RuntimeException {
		target.startJSON();
	}
	public void endJSON() throws RuntimeException {
		target.endJSON();
	}
	public boolean startObject() throws RuntimeException {
		return target.startObject();
	}
	public boolean endObject() throws RuntimeException {
		return target.endObject();
	}
	public boolean startObjectEntry(String key) throws RuntimeException {
		return target.startObjectEntry(key);
	}
	public boolean endObjectEntry() throws RuntimeException {
		return target.endObjectEntry();
	}
	public boolean startArray() throws RuntimeException {
		return target.startArray();
	}
	public boolean endArray() throws RuntimeException {
		return target.endArray();
	}
	public boolean primitive(Object value) throws RuntimeException {
		return target.primitive(value);
	}
}
