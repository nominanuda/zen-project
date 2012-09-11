package com.nominanuda.dataobject.transform;

import com.nominanuda.dataobject.JsonContentHandler;

public class DevNullJsonContentHandler implements JsonContentHandler {

	@Override
	public void startJSON() throws RuntimeException {
	}

	@Override
	public void endJSON() throws RuntimeException {
	}

	@Override
	public boolean startObject() throws RuntimeException {
		return true;
	}

	@Override
	public boolean endObject() throws RuntimeException {
		return true;
	}

	@Override
	public boolean startObjectEntry(String key) throws RuntimeException {
		return true;
	}

	@Override
	public boolean endObjectEntry() throws RuntimeException {
		return true;
	}

	@Override
	public boolean startArray() throws RuntimeException {
		return true;
	}

	@Override
	public boolean endArray() throws RuntimeException {
		return true;
	}

	@Override
	public boolean primitive(Object value) throws RuntimeException {
		return true;
	}
}
