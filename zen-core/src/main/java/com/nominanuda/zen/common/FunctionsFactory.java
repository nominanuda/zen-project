package com.nominanuda.zen.common;

import java.util.function.Function;

import com.nominanuda.zen.stereotype.Factory;

public class FunctionsFactory<T, R> implements Factory<Functions<T, R>> {
	private final Factory<Function<T, ?>> fncFactory;
	private final Factory<Function<?, ?>>[] fncFactories;
	
	public FunctionsFactory(Factory<Function<T, ?>> fncFactory, Factory<Function<?, ?>>... fncFactories) {
		this.fncFactory = fncFactory;
		this.fncFactories = fncFactories;
	}
	
	@Override
	public Functions<T, R> get() {
		int l = fncFactories.length;
		Function<?, ?>[] fncs = new Function[l];
		for (int i = 0; i < l; i++) {
			fncs[i] = fncFactories[i].get();
		}
		return new Functions<T, R>(fncFactory.get(), fncs);
	}
}
