package com.nominanuda.lang;

import java.util.function.Function;

import com.nominanuda.lang.ObjectFactory;

public class FunctionsFactory<T, R> implements ObjectFactory<Functions<T, R>> {
	private final ObjectFactory<Function<T, ?>> fncFactory;
	private final ObjectFactory<Function<?, ?>>[] fncFactories;
	
	public FunctionsFactory(ObjectFactory<Function<T, ?>> fncFactory, ObjectFactory<Function<?, ?>>... fncFactories) {
		this.fncFactory = fncFactory;
		this.fncFactories = fncFactories;
	}
	
	@Override
	public Functions<T, R> getObject() {
		int l = fncFactories.length;
		Function<?, ?>[] fncs = new Function[l];
		for (int i = 0; i < l; i++) {
			fncs[i] = fncFactories[i].getObject();
		}
		return new Functions<T, R>(fncFactory.getObject(), fncs);
	}
}
