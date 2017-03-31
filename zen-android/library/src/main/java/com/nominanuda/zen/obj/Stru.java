package com.nominanuda.zen.obj;

/**
 * Created by azum on 22/03/17.
 */

public interface Stru {
	public boolean isObj();
	public Obj asObj() throws ClassCastException;
	public boolean isArr();
	public Arr asArr() throws ClassCastException;
	String toString();
}
