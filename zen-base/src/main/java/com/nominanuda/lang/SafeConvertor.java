package com.nominanuda.lang;

public interface SafeConvertor<X, Y> extends ObjectConvertor<X, Y, NoException>, Fun1<X, Y> {
}
