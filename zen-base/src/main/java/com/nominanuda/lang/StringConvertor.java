package com.nominanuda.lang;

import com.nominanuda.code.ThreadSafe;

public interface StringConvertor extends SafeConvertor<String, String> {
	
	StringConvertor IDENTITY = new StringConvertor() {
		public boolean canConvert(Object o) {
			return o != null  && o instanceof String;
		}
		public String apply(String x) throws NullPointerException {
			return Check.notNull(x);
		}
	};
}
