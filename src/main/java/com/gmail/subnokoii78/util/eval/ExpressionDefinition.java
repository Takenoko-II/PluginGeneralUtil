package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.*;

public abstract class ExpressionDefinition<T, U> {
    private ExpressionDefinition() {}

    public boolean isConst() {
        return false;
    }

    public boolean isFunction() {
        return false;
    }

    public boolean isOperator() {
        return false;
    }

    public boolean isSelfOperator() {
        return false;
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

    public static final ExpressionDefinition<Number, Double> CONSTANT = new ExpressionDefinition<>() {
        @Override
        public @NotNull Double constant(@NotNull Number value) {
            return value.doubleValue();
        }

        @Override
        public boolean isConst() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleSupplier, Function<List<Double>, Double>> FUNCTION_NO_ARG = new ExpressionDefinition<>() {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull DoubleSupplier supplier) {
            return list -> {
                if (!list.isEmpty()) {
                    throw new CalcExpEvaluationException("引数の数は0つが期待されています");
                }
                return supplier.getAsDouble();
            };
        }

        @Override
        public boolean isFunction() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleUnaryOperator, Function<List<Double>, Double>> FUNCTION_1_ARG = new ExpressionDefinition<>() {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull DoubleUnaryOperator unaryOperator) {
            return list -> {
                if (list.size() != 1) {
                    throw new CalcExpEvaluationException("引数の数は1つが期待されています");
                }
                return unaryOperator.applyAsDouble(list.getFirst());
            };
        }

        @Override
        public boolean isFunction() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleBinaryOperator, Function<List<Double>, Double>> FUNCTION_2_ARG = new ExpressionDefinition<>() {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull DoubleBinaryOperator binaryOperator) {
            return list -> {
                if (list.size() != 2) {
                    throw new CalcExpEvaluationException("引数の数は2つが期待されています");
                }
                return binaryOperator.applyAsDouble(list.get(0), list.get(1));
            };
        }

        @Override
        public boolean isFunction() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleBinaryOperator, DoubleBinaryOperator> OPERATOR_POLYNOMIAL = new ExpressionDefinition<>() {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleBinaryOperator, DoubleBinaryOperator> OPERATOR_MONOMIAL = new ExpressionDefinition<>() {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleBinaryOperator, DoubleBinaryOperator> OPERATOR_FACTOR = new ExpressionDefinition<>() {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final ExpressionDefinition<DoubleUnaryOperator, DoubleUnaryOperator> SELF_OPERATOR_NUMBER_SUFFIX = new ExpressionDefinition<>() {
        @Override
        public @NotNull DoubleUnaryOperator selfOperator(@NotNull DoubleUnaryOperator unaryOperator) {
            return unaryOperator;
        }

        @Override
        public boolean isSelfOperator() {
            return true;
        }
    };
}
