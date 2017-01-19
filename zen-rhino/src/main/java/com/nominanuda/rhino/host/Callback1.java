package com.nominanuda.rhino.host;

public abstract class Callback1<RET, P1> extends AbsCallback<RET> {
	protected abstract RET callback(P1 p1);

	@Override
	RET call(Object... args) {
		return args.length == 1 ? callback((P1) args[0]) : null;
	}
}
