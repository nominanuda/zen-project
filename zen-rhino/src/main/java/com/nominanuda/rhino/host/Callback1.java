package com.nominanuda.rhino.host;

public abstract class Callback1<RET, P1> extends AbsCallback<RET> {
	private static final long serialVersionUID = 23893249874234551L;

	protected abstract RET callback(P1 p1);

	@SuppressWarnings("unchecked")
	@Override
	RET call(Object... args) {
		return args.length == 1 ? callback((P1) args[0]) : null;
	}
}
