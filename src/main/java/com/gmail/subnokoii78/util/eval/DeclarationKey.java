package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.*;

public abstract class DeclarationKey<T> {
    private final DeclarationCategory category;

    private DeclarationKey(@NotNull DeclarationCategory category) {
        this.category = category;
    }

    public @NotNull DeclarationCategory getCategory() {
        return category;
    }

    public @NotNull Double constant(@NotNull T value) throws IllegalStateException {
        throw new IllegalStateException();
    }

    public @NotNull Function<List<Double>, Double> function(@NotNull T value) throws IllegalStateException {
        throw new IllegalStateException();
    }

    public @NotNull DoubleBinaryOperator operator(@NotNull T value) throws IllegalStateException {
        throw new IllegalStateException();
    }

    public @NotNull DoubleUnaryOperator selfOperator(@NotNull T value) throws IllegalStateException {
        throw new IllegalStateException();
    }

    public static final DeclarationKey<Number> CONSTANT = new DeclarationKey<>(DeclarationCategory.CONSTANT) {
        @Override
        public @NotNull Double constant(@NotNull Number value) {
            return value.doubleValue();
        }
    };

    public static final DeclarationKey<Function<List<Double>, Double>> FUNCTION_VARIABLE_LENGTH_ARGS = new DeclarationKey<>(DeclarationCategory.FUNCTION) {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull Function<List<Double>, Double> function) {
            return function;
        }
    };

    public static final DeclarationKey<DoubleSupplier> FUNCTION_NO_ARGS = new DeclarationKey<>(DeclarationCategory.FUNCTION) {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull DoubleSupplier supplier) {
            return list -> {
                if (!list.isEmpty()) {
                    throw new CalcExpEvaluationException("引数の数は0つが期待されています");
                }
                return supplier.getAsDouble();
            };
        }
    };

    public static final DeclarationKey<DoubleUnaryOperator> FUNCTION_1_ARG = new DeclarationKey<>(DeclarationCategory.FUNCTION) {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull DoubleUnaryOperator unaryOperator) {
            return list -> {
                if (list.size() != 1) {
                    throw new CalcExpEvaluationException("引数の数は1つが期待されています");
                }
                return unaryOperator.applyAsDouble(list.getFirst());
            };
        }
    };

    public static final DeclarationKey<DoubleBinaryOperator> FUNCTION_2_ARGS = new DeclarationKey<>(DeclarationCategory.FUNCTION) {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull DoubleBinaryOperator binaryOperator) {
            return list -> {
                if (list.size() != 2) {
                    throw new CalcExpEvaluationException("引数の数は2つが期待されています");
                }
                return binaryOperator.applyAsDouble(list.get(0), list.get(1));
            };
        }
    };

    public static final DeclarationKey<DoubleBinaryOperator> OPERATOR_POLYNOMIAL = new DeclarationKey<>(DeclarationCategory.OPERATOR) {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }
    };

    public static final DeclarationKey<DoubleBinaryOperator> OPERATOR_MONOMIAL = new DeclarationKey<>(DeclarationCategory.OPERATOR) {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }
    };

    public static final DeclarationKey<DoubleBinaryOperator> OPERATOR_FACTOR = new DeclarationKey<>(DeclarationCategory.OPERATOR) {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }
    };

    public static final DeclarationKey<DoubleUnaryOperator> SELF_OPERATOR_NUMBER_SUFFIX = new DeclarationKey<>(DeclarationCategory.SELF_OPERATOR) {
        @Override
        public @NotNull DoubleUnaryOperator selfOperator(@NotNull DoubleUnaryOperator unaryOperator) {
            return unaryOperator;
        }
    };

    public enum DeclarationCategory {
        CONSTANT,
        FUNCTION,
        OPERATOR,
        SELF_OPERATOR
    }
}
