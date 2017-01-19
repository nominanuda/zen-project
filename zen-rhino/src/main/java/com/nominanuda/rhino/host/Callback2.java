package com.nominanuda.rhino.host;

public abstract class Callback2<RET, P1, P2> extends AbsCallback<RET> {
	protected abstract RET callback(P1 p1, P2 p2);

	@Override
	RET call(Object... args) {
		return args.length == 2 ? callback((P1) args[0], (P2) args[1]) : null;
	}
}
