package com.nominanuda.dataobject;


public @interface Struct {
	boolean listing() default false;
	String listingPath() default "results";
	Class<?> cls() default DataStruct.class;
	
	String results = "results";

}
