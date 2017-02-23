package com.nominanuda.web.mvc;

import com.nominanuda.zen.obj.Stru;

public class PathAndJsonViewSpec implements ViewSpec {
	private String path;
	private Stru model;

	public PathAndJsonViewSpec(String path, Stru model) {
		this.path = path;
		this.model = model;
	}

	public String getPath() {
		return path;
	}
	public Stru getModel() {
		return model;
	}
}
