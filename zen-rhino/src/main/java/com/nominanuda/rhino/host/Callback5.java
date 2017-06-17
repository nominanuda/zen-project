package com.nominanuda.rhino.host;

public abstract class Callback5<RET, P1, P2, P3, P4, P5> extends AbsCallback<RET> {
	private static final long serialVersionUID = 23893249874234599L;
	protected abstract RET callback(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

	@SuppressWarnings("unchecked")
	@Override
	RET call(Object... args) {
		return args.length == 5 ? callback((P1) args[0], (P2) args[1], (P3) args[2], (P4) args[3], (P5) args[4]) : null;
	}
}
