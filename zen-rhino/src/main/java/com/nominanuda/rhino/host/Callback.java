package com.nominanuda.rhino.host;


public abstract class Callback<RET, T> extends AbsCallback<RET> {
	protected abstract RET callback(T... args);
	protected abstract T[] cast(Object[] args, int l);
	
	@Override
	RET call(Object... args) {
		return callback(cast(args, args.length));
	}
}
