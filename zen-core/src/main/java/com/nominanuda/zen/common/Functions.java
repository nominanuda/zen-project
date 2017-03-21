package com.nominanuda.zen.common;

import java.util.function.Function;

public class Functions<T, R> implements Function<T, R> {
	static class Chain<T> {
		private final T param;
		
		public Chain(T param) {
			this.param = param;
		}
		
		public <R> Chain<R> apply(Function<T, R> fnc) {
			return new Chain<R>(fnc.apply(param));
		}
		
		public T get() {
			return param;
		}
	}
	
	
	private final Function<T, ?> fnc;
	private final Function<?, ?>[] fncs;
	
	public Functions(Function<T, ?> fnc, Function<?, ?>... fncs) {
		this.fnc = fnc;
		this.fncs = fncs;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public R apply(T t) {
		Chain<?> chain = new Chain<T>(t).apply(fnc);
		for (Function f : fncs) {
			chain = chain.apply(f);
		}
		return (R) chain.get();
	}
}
