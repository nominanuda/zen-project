package com.nominanuda.dataobject.transform;

import com.nominanuda.dataobject.JsonContentHandler;

public interface JsonTransformer extends JsonContentHandler {
	void setTarget(JsonContentHandler target);
}
