package com.nominanuda.rhino;

import java.math.BigDecimal;

import com.nominanuda.zen.common.Ex.NoException;

public class BigDecimalCoercer implements ObjectCoercer<Number, BigDecimal, NoException> {

	@Override
	public BigDecimal apply(Number x) throws NoException {
		return new BigDecimal(x.toString()); // going through String to have correct decimals parsing
	}

	@Override
	public boolean canConvert(Object o) {
		return o instanceof Number;
	}

}
