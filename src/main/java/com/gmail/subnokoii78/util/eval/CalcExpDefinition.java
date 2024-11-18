package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public final class CalcExpDefinition<T> {
    private final String name;

    private CalcExpDefinition(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }

    public static final CalcExpDefinition<DoubleSupplier> FUNCTION_SUPPLIER = new CalcExpDefinition<>("Function Supplier");

    public static final CalcExpDefinition<DoubleUnaryOperator> FUNCTION_UNARY_OPERATOR = new CalcExpDefinition<>("Function UnaryOperator");

    public static final CalcExpDefinition<BinaryOperator<Double>> FUNCTION_BINARY_OPERATOR = new CalcExpDefinition<>("Function BinaryOperator");

    public static final CalcExpDefinition<List<Double>> FUNCTION = new CalcExpDefinition<>("Function");

    public static final CalcExpDefinition<Double> CONSTANT = new CalcExpDefinition<>("Constant");

    public static final CalcExpDefinition<BinaryOperator<Double>> OPERATOR_POLYNOMIAL = new CalcExpDefinition<>("Operator Polynomial");

    public static final CalcExpDefinition<BinaryOperator<Double>> OPERATOR_MONOMIAL = new CalcExpDefinition<>("Operator Monomial");

    public static final CalcExpDefinition<BinaryOperator<Double>> OPERATOR_FACTOR = new CalcExpDefinition<>("Operator Factor");

    public static final CalcExpDefinition<BinaryOperator<Double>> OPERATOR_NUMBER_SUFFIX = new CalcExpDefinition<>("Operator NumberSuffix");
}
