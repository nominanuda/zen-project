package com.nominanuda.rhino.host;

public abstract class Callback3<RET, P1, P2, P3> extends AbsCallback<RET> {
	private static final long serialVersionUID = 23893249874234556L;
	protected abstract RET callback(P1 p1, P2 p2, P3 p3);

	@SuppressWarnings("unchecked")
	@Override
	RET call(Object... args) {
		return args.length == 3 ? callback((P1) args[0], (P2) args[1], (P3) args[2]) : null;
	}
}
