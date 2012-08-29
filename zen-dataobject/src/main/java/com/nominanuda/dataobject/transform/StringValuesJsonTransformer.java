package com.nominanuda.dataobject.transform;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import com.nominanuda.lang.Check;

public class StringValuesJsonTransformer extends BaseJsonTransformer {
	
	@Override
	public boolean primitive(Object value) throws RuntimeException {
		if(value == null || value instanceof String) {
			return super.primitive(value);
		} else if(value instanceof Boolean) {
			return super.primitive(value.toString());
		} else {
			return super.primitive(STRUCT.numberToString(
					Check.illegalstate.assertInstanceOf(value, Number.class)));
		}
	}

}
