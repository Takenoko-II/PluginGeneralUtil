package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

@FunctionalInterface
public interface CalcExpFunction extends Function<List<Double>, Double> {
    static @NotNull CalcExpFunction of(@NotNull DoubleSupplier supplier) {
        return list -> {
            if (!list.isEmpty()) {
                throw new CalcExpEvalException("引数の数は0つが期待されています");
            }
            return supplier.getAsDouble();
        };
    }

    static @NotNull CalcExpFunction of(@NotNull DoubleUnaryOperator unaryOperator) {
        return list -> {
            if (list.size() != 1) {
                throw new CalcExpEvalException("引数の数は1つが期待されています");
            }
            return unaryOperator.applyAsDouble(list.getFirst());
        };
    }

    static @NotNull CalcExpFunction of(@NotNull BinaryOperator<Double> binaryOperator) {
        return list -> {
            if (list.size() != 2) {
                throw new CalcExpEvalException("引数の数は2つが期待されています");
            }
            return binaryOperator.apply(list.get(0), list.get(1));
        };
    }
}
