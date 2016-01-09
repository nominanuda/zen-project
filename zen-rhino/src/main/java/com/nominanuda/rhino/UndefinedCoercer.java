package com.nominanuda.rhino;

import org.mozilla.javascript.Undefined;

import com.nominanuda.lang.NoException;
import com.nominanuda.lang.ObjectConvertor;

public class UndefinedCoercer implements ObjectConvertor<Undefined, Void, NoException> {

	@Override
	public Void apply(Undefined x) throws NoException {
		return null;
	}

	@Override
	public boolean canConvert(Object o) {
		return (o instanceof Undefined);
	}
}
