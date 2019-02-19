package com.nominanuda.zen.obj.wrap.getter;

import com.nominanuda.zen.obj.wrap.Wrap;

import org.json.JSONObject;

import java.lang.reflect.Method;

public interface IGetter {
	void init(Wrap wf);
	
	boolean supports(Method getter);
	
	Object extract(JSONObject o, Method getter) throws Throwable;
}
