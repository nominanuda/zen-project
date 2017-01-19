package com.nominanuda.rhino.host;

public abstract class Callback4<RET, P1, P2, P3, P4> extends AbsCallback<RET> {
	protected abstract RET callback(P1 p1, P2 p2, P3 p3, P4 p4);

	@Override
	RET call(Object... args) {
		return args.length == 4 ? callback((P1) args[0], (P2) args[1], (P3) args[2], (P4) args[3]) : null;
	}
}
