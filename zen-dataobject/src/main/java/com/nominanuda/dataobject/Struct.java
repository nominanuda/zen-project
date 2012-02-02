package com.nominanuda.dataobject;


public @interface Struct {
	boolean listing() default false;
	String listingPath() default results;
	Class<?> cls();
	
	String results = "results";

}
