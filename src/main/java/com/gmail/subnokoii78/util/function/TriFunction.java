package com.gmail.subnokoii78.util.function;

@FunctionalInterface
public interface TriFunction<S, T, U, R> {
    R apply(S s, T t, U u);
}
