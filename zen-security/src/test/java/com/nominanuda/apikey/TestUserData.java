package com.nominanuda.apikey;

import com.nominanuda.zen.obj.wrap.ObjWrapper;


public interface TestUserData extends ObjWrapper {
	String subscription();
	TestUserData subscription(String subscription);

	String deviceId();
	TestUserData deviceId(String deviceId);

	String appId();
	TestUserData appId(String appId);

	boolean sample();
	TestUserData sample(boolean sample);

	String auth();
	TestUserData auth(String auth);

	String pin();
	TestUserData pin(String pin);

	String cli();
	TestUserData cli(String cli);
}