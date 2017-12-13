package com.nominanuda.zen.obj.wrap.getter;

import java.lang.reflect.Method;

import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.Wrap;

public interface IGetter {
	void init(Wrap wf);
	
	boolean supports(Method getter);
	
	Object extract(Obj o, Method getter) throws Throwable;
}
