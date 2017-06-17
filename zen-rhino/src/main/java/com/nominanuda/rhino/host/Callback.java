package com.nominanuda.rhino.host;


public abstract class Callback<RET, T> extends AbsCallback<RET> {
	private static final long serialVersionUID = 78687868663224L;

	protected abstract RET callback(@SuppressWarnings("unchecked") T... args);
	protected abstract T[] cast(Object[] args, int l);
	
	@Override
	RET call(Object... args) {
		return callback(cast(args, args.length));
	}
}
