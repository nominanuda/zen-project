package com.nominanuda.lang;

import java.util.function.Function;

public interface SafeConvertor<X, Y> extends ObjectConvertor<X, Y, NoException>, Function<X, Y> {
}
