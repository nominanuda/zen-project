package com.nominanuda.dataobject.domain;

import java.lang.reflect.Proxy;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;

public class NakedStructFactory {

	@Nullable public <T extends NakedObject> T create(@Nullable DataStruct<?> struct, Class<T> type) throws IllegalArgumentException {
		Check.illegalargument.assertTrue(
			NakedStruct.class.isAssignableFrom(type));
		if(struct == null) {
			return null;
		}
		try {
			if(type.isInterface()) {
				NoOpNakedStruct proxy = new NoOpNakedStruct(struct);
				Object o = Proxy.newProxyInstance(type.getClassLoader(),
						new Class[] { type }, proxy);
				return type.cast(o);
			} else if(BaseNakedObject.class.isAssignableFrom(type)) {
					return type.getConstructor(DataObject.class)
								.newInstance((DataObject)struct);
			} else if(BaseNakedArray.class.isAssignableFrom(type)) {
				return type.getConstructor(DataArray.class)
							.newInstance((DataArray)struct);
			} else {
				throw new IllegalArgumentException(
					"cannot build object of type "+type.getName());
			}
		} catch (Exception e) {
			if(e instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)e;
			} else {
				throw new IllegalArgumentException(e);
			}
		}
	}
}
