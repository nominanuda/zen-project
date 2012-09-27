package com.nominanuda.dataobject.schema;

import com.nominanuda.code.Nullable;

public class ExistentialPredicate {
	private boolean optional = false;
	private boolean nullable = true;

	public ExistentialPredicate(@Nullable String p) {
		if(p != null) {
			optional = p.contains("?");
			nullable = !p.contains("!");
		}
	}

	public ExistentialPredicate() {
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isNullable() {
		return nullable;
	}
}
