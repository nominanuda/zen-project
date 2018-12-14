package com.nominanuda.store.api;

import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.obj.Obj;


/**
 * Simple key value storage interface where Obj values are associated with a single key.
 * @see com.nominanuda.Obj.Obj
 */
public interface JsonKeyValueStore extends KeyValueStore<String, Obj, NoException> {
	
}
