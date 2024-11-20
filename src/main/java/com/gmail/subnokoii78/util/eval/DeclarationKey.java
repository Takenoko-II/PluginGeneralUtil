package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.*;

public abstract class DeclarationKey<T> {
    private DeclarationKey() {}

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

    public static final DeclarationKey<Number> CONSTANT = new DeclarationKey<>() {
        @Override
        public @NotNull Double constant(@NotNull Number value) {
            return value.doubleValue();
        }

        @Override
        public boolean isConst() {
            return true;
        }
    };

    public static final DeclarationKey<Function<List<Double>, Double>> FUNCTION_VARIABLE_LENGTH_ARGS = new DeclarationKey<>() {
        @Override
        public @NotNull Function<List<Double>, Double> function(@NotNull Function<List<Double>, Double> function) {
            return function;
        }

        @Override
        public boolean isFunction() {
            return true;
        }
    };

    public static final DeclarationKey<DoubleSupplier> FUNCTION_NO_ARGS = new DeclarationKey<>() {
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

    public static final DeclarationKey<DoubleUnaryOperator> FUNCTION_1_ARG = new DeclarationKey<>() {
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

    public static final DeclarationKey<DoubleBinaryOperator> FUNCTION_2_ARGS = new DeclarationKey<>() {
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

    public static final DeclarationKey<DoubleBinaryOperator> OPERATOR_POLYNOMIAL = new DeclarationKey<>() {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final DeclarationKey<DoubleBinaryOperator> OPERATOR_MONOMIAL = new DeclarationKey<>() {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final DeclarationKey<DoubleBinaryOperator> OPERATOR_FACTOR = new DeclarationKey<>() {
        @Override
        public @NotNull DoubleBinaryOperator operator(@NotNull DoubleBinaryOperator binaryOperator) {
            return binaryOperator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final DeclarationKey<DoubleUnaryOperator> SELF_OPERATOR_NUMBER_SUFFIX = new DeclarationKey<>() {
        @Override
        public @NotNull DoubleUnaryOperator selfOperator(@NotNull DoubleUnaryOperator unaryOperator) {
            return unaryOperator;
        }

        @Override
        public boolean isSelfOperator() {
            return true;
        }
    };

    public static final DeclarationKey<Void> ABSTRACT_FUNCTION = new DeclarationKey<>() {
        @Override
        public boolean isFunction() {
            return true;
        }
    };

    public static final DeclarationKey<Void> ABSTRACT_OPERATOR = new DeclarationKey<>() {
        @Override
        public boolean isOperator() {
            return true;
        }
    };

    public static final DeclarationKey<Void> ABSTRACT_SELF_OPERATOR = new DeclarationKey<>() {
        @Override
        public boolean isSelfOperator() {
            return true;
        }
    };
}
