package com.nominanuda.dataobject;

public interface JsonStreamer {
	void stream(JsonContentHandler jch) throws RuntimeException;
}
