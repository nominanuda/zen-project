package com.nominanuda.rhino.lang;

import static com.nominanuda.dataobject.WrappingFactory.WF;

import java.util.function.Function;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectWrapper;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Check;

public class Wrapper2Script2Wrapper<WRAPPER extends DataObjectWrapper> implements Function<WRAPPER, WRAPPER> {
	private final Function<DataObject, DataStruct> fnc;
	private final Class<WRAPPER> clz;
	
	
	public Wrapper2Script2Wrapper(Function<DataObject, DataStruct> fnc, Class<WRAPPER> clz) {
		this.fnc = Check.notNull(fnc, "wrapped script is null!");
		this.clz = clz;
	}
	

	@Override
	public WRAPPER apply(WRAPPER t) {
		if (t != null) {
			DataStruct result = fnc.apply(t.unwrap());
			if (result != null) {
				return WF.wrap(result.asObject(), clz);
			}
		}
		return null;
	}
}
