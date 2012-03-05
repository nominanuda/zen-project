package com.nominanuda.lang;

public interface SafeConvertor<X, Y> extends ObjectConvertor<X, Y, NoException>, Arity1Fun<X, Y> {
}
