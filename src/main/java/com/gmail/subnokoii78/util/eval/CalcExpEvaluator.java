package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;

public final class CalcExpEvaluator {
    private static final Set<Character> IGNORED = Set.of(' ', '\n');

    private static final List<Character> SIGNS = List.of('+', '-');

    private static final Set<Character> NUMBERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final Function<String, Double> NUMBER_PARSER = Double::parseDouble;

    private static final char DECIMAL_POINT = '.';

    private static final char FUNCTION_ARGUMENT_SEPARATOR = ',';

    private static final char PARENTHESIS_START = '(';

    private static final char PARENTHESIS_END = ')';

    private final Map<String, DoubleBinaryOperator> MONOMIAL_OPERATORS = new HashMap<>(Map.of(
        "*", (a, b) -> a * b,
        "/", (a, b) -> {
            if (a == 0 && b == 0) {
                throw new CalcExpEvaluationException("式 '0 / 0'はNaNを返します");
            }

            return a / b;
        },
        "%", (a, b) -> {
            if (a == 0 && b == 0) {
                throw new CalcExpEvaluationException("式 '0 % 0'はNaNを返します");
            }

            return a % b;
        }
    ));

    private final Map<String, DoubleBinaryOperator> POLYNOMIAL_OPERATORS = new HashMap<>(Map.of(
        "+", Double::sum,
        "-", (a, b) -> a - b
    ));

    private final Map<String, DoubleBinaryOperator> FACTOR_OPERATORS = new HashMap<>(Map.of(
        "^", Math::pow
    ));

    private final Map<String, DoubleUnaryOperator> NUMBER_SUFFIX_OPERATOR = new HashMap<>(Map.of(
        "!", value -> {
            if (value != (double) (int) value) {
                throw new CalcExpEvaluationException("階乗演算子は実質的な整数の値にのみ使用できます");
            }
            else if (value < 0) {
                throw new CalcExpEvaluationException("階乗演算子は負の値に使用できません");
            }
            else if (value > 127) {
                throw new CalcExpEvaluationException("階乗演算子は127!を超えた値を計算できないよう制限されています");
            }

            double result = 1;

            for (int i = 2; i <= value; i++) {
                result *= i;
            }

            return result;
        }
    ));

    private final Map<String, Function<List<Double>, Double>> FUNCTIONS = new HashMap<>();

    private final Map<String, Double> CONSTANTS = new HashMap<>();

    private String expression;

    private int location = 0;

    public CalcExpEvaluator() {}

    private boolean isOver() {
        return location >= expression.length();
    }

    private char next() {
        if (isOver()) {
            throw new CalcExpEvaluationException("文字数を超えた位置へのアクセスが発生しました");
        }

        final char current = expression.charAt(location++);

        if (IGNORED.contains(current)) return next();

        return current;
    }

    private void beforeWhitespace() {
        if (isOver()) return;

        final char current = expression.charAt(location++);

        if (IGNORED.contains(current)) {
            beforeWhitespace();
        }
        else {
            location--;
        }
    }

    private boolean nextIf(char next) {
        if (location >= expression.length()) {
            return false;
        }

        final char current = expression.charAt(location);

        if (current == next) {
            location++;
            return true;
        }

        return false;
    }

    private boolean nextIf(@NotNull String next) {
        if (isOver()) return false;

        final String str = expression.substring(location);

        if (str.startsWith(next)) {
            location += next.length();
            beforeWhitespace();
            return true;
        }

        return false;
    }

    private double number() {
        final char init = next();
        final StringBuilder stringBuilder = new StringBuilder();

        if (SIGNS.contains(init)) {
            stringBuilder.append(init);
            beforeWhitespace();

            if (isOver()) {
                throw new CalcExpEvaluationException("符号の後には数が必要です");
            }
        }
        else {
            location--;
        }

        final char futureNext = expression.charAt(location);

        if (isFunction()) {
            stringBuilder.append(getFunction().apply(arguments()));
        }
        else if (isConst()) {
            stringBuilder.append(getConst());
        }
        else if (SIGNS.contains(futureNext) || futureNext == PARENTHESIS_START) {
            final double value = polynomial();

            if (stringBuilder.isEmpty()) {
                stringBuilder.append(value);
            }
            else {
                final char sign = stringBuilder.charAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.append(mergeSign(sign, value));
            }
        }
        else {
            boolean dotAlreadyAppended = false;
            while (!isOver()) {
                final char current = next();

                if (NUMBERS.contains(current)) stringBuilder.append(current);
                else if (current == DECIMAL_POINT) {
                    if (dotAlreadyAppended) {
                        throw new CalcExpEvaluationException("無効な小数点を検知しました");
                    }

                    stringBuilder.append(current);
                    dotAlreadyAppended = true;
                }
                else {
                    location--;
                    break;
                }
            }
        }

        try {
            return NUMBER_PARSER.apply(stringBuilder.toString());
        }
        catch (NumberFormatException e) {
            throw new CalcExpEvaluationException("数値の解析に失敗しました: " + expression.substring(location), e);
        }
    }

    private double mergeSign(char sign, double value) {
        if (SIGNS.get(0).equals(sign)) {
            return value;
        }
        else if (SIGNS.get(1).equals(sign)) {
            return -value;
        }
        else {
            throw new CalcExpEvaluationException("無効な符号です: " + sign);
        }
    }

    private double monomial() {
        double value = operateIfSuffix(factor());

        a: while (!isOver()) {
            for (final String o : MONOMIAL_OPERATORS.keySet()) {
                if (nextIf(o)) {
                    value = MONOMIAL_OPERATORS.get(o).applyAsDouble(value, operateIfSuffix(factor()));
                    continue a;
                }
            }
            break;
        }

        return value;
    }

    private double polynomial() {
        double value = monomial();

        a: while (!isOver()) {
            for (final String o : POLYNOMIAL_OPERATORS.keySet()) {
                if (nextIf(o)) {
                    value = POLYNOMIAL_OPERATORS.get(o).applyAsDouble(value, monomial());
                    continue a;
                }
            }
            break;
        }

        return value;
    }

    private double operateIfSuffix(double num) {
        double value = num;

        a: while (!isOver()) {
            for (final String o : NUMBER_SUFFIX_OPERATOR.keySet()) {
                if (nextIf(o)) {
                    value = NUMBER_SUFFIX_OPERATOR.get(o).applyAsDouble(value);
                    continue a;
                }
            }

            for (final String o : FACTOR_OPERATORS.keySet()) {
                if (nextIf(o)) {
                    final double obj = factor();
                    value = FACTOR_OPERATORS.get(o).applyAsDouble(value, obj);
                    continue a;
                }
            }

            break;
        }

        return value;
    }

    private double factor() {
        final char current = next();

        if (current == PARENTHESIS_START) {
            double value = polynomial();
            if (isOver()) throw new CalcExpEvaluationException("括弧が閉じられていません");
            final char next = next();
            if (next == PARENTHESIS_END) {
                beforeWhitespace();
                return value;
            }
            else throw new CalcExpEvaluationException("括弧が閉じられていません: " + next);
        }
        else {
            location--;
            double num = number();

            if (Double.isNaN(num)) {
                throw new CalcExpEvaluationException("関数または定数からNaNが出力されました");
            }
            return num;
        }
    }

    private List<Double> arguments() {
        final char current = next();
        final List<Double> args = new ArrayList<>();

        if (current == PARENTHESIS_START) {
            if (nextIf(PARENTHESIS_END)) {
                return args;
            }

            while (true) {
                if (isOver()) throw new CalcExpEvaluationException("引数の探索中に文字列外に来ました");
                double value = polynomial();
                final char next = next();
                if (next == FUNCTION_ARGUMENT_SEPARATOR) {
                    args.add(value);
                }
                else if (next == PARENTHESIS_END) {
                    args.add(value);
                    return args;
                }
                else throw new CalcExpEvaluationException("関数の引数の区切りが見つかりません: " + next);
            }
        }
        else throw new CalcExpEvaluationException("関数の呼び出しには括弧が必要です: " + current);
    }

    private boolean isConst() {
        final String str = expression.substring(location);

        for (final String name : CONSTANTS.keySet()) {
            if (str.startsWith(name)) {
                return true;
            }
        }

        return false;
    }

    private Double getConst() {
        final String str = expression.substring(location);

        for (final String name : CONSTANTS.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            if (str.startsWith(name)) {
                location += name.length();
                return CONSTANTS.get(name);
            }
        }

        throw new CalcExpEvaluationException("定数を取得できませんでした");
    }

    private boolean isFunction() {
        final String str = expression.substring(location);

        for (final String name : FUNCTIONS.keySet()) {
            if (str.startsWith(name + PARENTHESIS_START)) {
                return true;
            }
        }

        return false;
    }

    private Function<List<Double>, Double> getFunction() {
        final String str = expression.substring(location);

        for (final String name : FUNCTIONS.keySet()) {
            if (str.startsWith(name + PARENTHESIS_START)) {
                location += name.length();
                return FUNCTIONS.get(name);
            }
        }

        throw new CalcExpEvaluationException("関数を取得できませんでした");
    }

    private void checkExtra() {
        if (!expression.substring(location).isEmpty()) {
            throw new CalcExpEvaluationException("式の終了後に無効な文字を検出しました: " + expression.substring(location));
        }
    }

    public <T, U> boolean isDefined(@NotNull ExpressionDefinition<T, U> type, @NotNull String name) {
        if (type.isConst()) {
            return CONSTANTS.containsKey(name);
        }
        else if (type.isFunction()) {
            return FUNCTIONS.containsKey(name);
        }
        else if (type.isOperator()) {
            return POLYNOMIAL_OPERATORS.containsKey(name)
                || MONOMIAL_OPERATORS.containsKey(name)
                || FACTOR_OPERATORS.containsKey(name);
        }
        else if (type.isSelfOperator()) {
            return NUMBER_SUFFIX_OPERATOR.containsKey(name);
        }
        else return false;
    }

    private <T, U> void throwIfDefined(@NotNull ExpressionDefinition<T, U> type, @NotNull String name) throws IllegalArgumentException {
        if (isDefined(type, name)) {
            throw new IllegalArgumentException("その名前は既に使用されています: " + name);
        }
    }

    public double evaluate(@NotNull String expression) throws CalcExpEvaluationException {
        this.expression = expression;
        if (isOver()) throw new CalcExpEvaluationException("空文字は計算できません");
        final double value = polynomial();
        checkExtra();
        location = 0;

        if (Double.isNaN(value)) {
            throw new CalcExpEvaluationException("式からNaNが出力されました");
        }

        return value;
    }

    public <T, U> void define(@NotNull String name, @NotNull ExpressionDefinition<T, U> type, @NotNull T value) {
        throwIfDefined(type, name);

        if (type.isConst()) {
            CONSTANTS.put(name, type.constant(value));
        }
        else if (type.isFunction()) {
            FUNCTIONS.put(name, type.function(value));
        }
        else if (type.isOperator()) {
            final Map<String, DoubleBinaryOperator> map;

            if (type == ExpressionDefinition.OPERATOR_POLYNOMIAL) map = POLYNOMIAL_OPERATORS;
            else if (type == ExpressionDefinition.OPERATOR_MONOMIAL) map = MONOMIAL_OPERATORS;
            else if (type == ExpressionDefinition.OPERATOR_FACTOR) map = FACTOR_OPERATORS;
            else throw new IllegalArgumentException("不明な演算子定義タイプです");

            map.put(name, type.operator(value));
        }
        else if (type.isSelfOperator()) {
            final Map<String, DoubleUnaryOperator> map;

            if (type == ExpressionDefinition.SELF_OPERATOR_NUMBER_SUFFIX) map = NUMBER_SUFFIX_OPERATOR;
            else throw new IllegalArgumentException("不明な演算子定義タイプです");

            map.put(name, type.selfOperator(value));
        }
        else throw new IllegalArgumentException("不明な演算子定義タイプです");
    }

    public <T, U> void undefine(@NotNull ExpressionDefinition<T, U> type, @NotNull String name) {
        if (isDefined(type, name)) {
            if (type.isConst()) {
                CONSTANTS.remove(name);
            }
            else if (type.isFunction()) {
                FUNCTIONS.remove(name);
            }
            else if (type.isOperator()) {
                POLYNOMIAL_OPERATORS.remove(name);
                MONOMIAL_OPERATORS.remove(name);
                FACTOR_OPERATORS.remove(name);
            }
            else if (type.isSelfOperator()) {
                NUMBER_SUFFIX_OPERATOR.remove(name);
            }
        }
    }

    @Override
    public @NotNull String toString() {
        return "<CalcExpEvaluator>";
    }

    public static @NotNull CalcExpEvaluator getDefaultEvaluator() {
        final CalcExpEvaluator evaluator = new CalcExpEvaluator();

        evaluator.define("NaN", ExpressionDefinition.CONSTANT, Double.NaN);
        evaluator.define("PI", ExpressionDefinition.CONSTANT, Math.PI);
        evaluator.define("TAU", ExpressionDefinition.CONSTANT, Math.TAU);
        evaluator.define("E", ExpressionDefinition.CONSTANT, Math.E);
        evaluator.define("Infinity", ExpressionDefinition.CONSTANT, Double.POSITIVE_INFINITY);

        evaluator.define("random", ExpressionDefinition.FUNCTION_NO_ARG, Math::random);

        evaluator.define("sqrt", ExpressionDefinition.FUNCTION_1_ARG, Math::sqrt);
        evaluator.define("cbrt", ExpressionDefinition.FUNCTION_1_ARG, Math::cbrt);
        evaluator.define("abs", ExpressionDefinition.FUNCTION_1_ARG, Math::abs);
        evaluator.define("floor", ExpressionDefinition.FUNCTION_1_ARG, Math::floor);
        evaluator.define("ceil", ExpressionDefinition.FUNCTION_1_ARG, Math::ceil);
        evaluator.define("round", ExpressionDefinition.FUNCTION_1_ARG, Math::round);
        evaluator.define("sin", ExpressionDefinition.FUNCTION_1_ARG, Math::sin);
        evaluator.define("cos", ExpressionDefinition.FUNCTION_1_ARG, Math::cos);
        evaluator.define("tan", ExpressionDefinition.FUNCTION_1_ARG, Math::tan);
        evaluator.define("asin", ExpressionDefinition.FUNCTION_1_ARG, Math::asin);
        evaluator.define("acos", ExpressionDefinition.FUNCTION_1_ARG, Math::acos);
        evaluator.define("atan", ExpressionDefinition.FUNCTION_1_ARG, Math::atan);
        evaluator.define("exp", ExpressionDefinition.FUNCTION_1_ARG, Math::exp);
        evaluator.define("to_degrees", ExpressionDefinition.FUNCTION_1_ARG, Math::toDegrees);
        evaluator.define("to_radians", ExpressionDefinition.FUNCTION_1_ARG, Math::toRadians);
        evaluator.define("log10", ExpressionDefinition.FUNCTION_1_ARG, Math::log10);

        evaluator.define("log", ExpressionDefinition.FUNCTION_2_ARG, (a, b) -> Math.log(b) / Math.log(a));
        evaluator.define("atan2", ExpressionDefinition.FUNCTION_2_ARG, Math::atan2);
        evaluator.define("min", ExpressionDefinition.FUNCTION_2_ARG, Math::min);
        evaluator.define("max", ExpressionDefinition.FUNCTION_2_ARG, Math::max);
        evaluator.define("pow", ExpressionDefinition.FUNCTION_2_ARG, Math::pow);

        return evaluator;
    }
}
