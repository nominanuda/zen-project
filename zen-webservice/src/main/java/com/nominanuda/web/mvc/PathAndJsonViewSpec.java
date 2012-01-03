package com.nominanuda.web.mvc;

import com.nominanuda.dataobject.DataStruct;

public class PathAndJsonViewSpec implements ViewSpec {
	private String path;
	private DataStruct<?> model;

	public PathAndJsonViewSpec(String path, DataStruct<?> model) {
		this.path = path;
		this.model = model;
	}

	public String getPath() {
		return path;
	}
	public DataStruct<?> getModel() {
		return model;
	}
}
