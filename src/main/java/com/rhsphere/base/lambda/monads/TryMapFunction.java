package com.rhsphere.base.lambda.monads;


public interface TryMapFunction<T, R> {
    R apply(T t) throws Throwable;
}
