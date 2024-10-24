package com.gmail.subnokoii78.util.other;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;

public final class CalcExpEvaluator {
    private static final Set<Character> IGNORED = Set.of(' ', '\n');

    private static final List<Character> SIGNS = List.of('+', '-');

    private static final Set<Character> INTEGERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final char DECIMAL_POINT = '.';

    private static final char FUNCTION_ARGUMENT_SEPARATOR = ',';

    private static final char PARENTHESIS_START = '(';

    private static final char PARENTHESIS_END = ')';

    private final Map<Character, BinaryOperator<Double>> MONOMIAL_OPERATORS = new HashMap<>(Map.of(
        '*', (a, b) -> a * b,
        '/', (a, b) -> {
            if (a == 0 && b == 0) {
                throw new CalcExpEvalException("式 '0 / 0'はNaNを返します");
            }

            return a / b;
        },
        '%', (a, b) -> {
            if (a == 0 && b == 0) {
                throw new CalcExpEvalException("式 '0 % 0'はNaNを返します");
            }

            return a % b;
        }
    ));

    private final Map<Character, BinaryOperator<Double>> POLYNOMIAL_OPERATORS = new HashMap<>(Map.of(
        '+', Double::sum,
        '-', (a, b) -> a - b
    ));

    private final Map<Character, BinaryOperator<Double>> FACTOR_OPERATORS = new HashMap<>(Map.of(
        '^', Math::pow
    ));

    private final Map<Character, DoubleUnaryOperator> NUMBER_SUFFIX_OPERATOR = new HashMap<>(Map.of(
        '!', value -> {
            if (value != (double) (int) value) {
                throw new CalcExpEvalException("階乗演算子は実質的な整数の値にのみ使用できます");
            }
            else if (value < 0) {
                throw new CalcExpEvalException("階乗演算子は負の値に使用できません");
            }
            else if (value > 127) {
                throw new CalcExpEvalException("階乗演算子は127!を超えた値を計算できないよう制限されています");
            }

            double result = 1;

            for (int i = 2; i <= value; i++) {
                result *= i;
            }

            return result;
        }
    ));

    private final Map<String, DoubleSupplier> ZERO_ARGUMENT_FUNCTIONS = new HashMap<>();

    private final Map<String, DoubleUnaryOperator> SINGLE_ARGUMENT_FUNCTIONS = new HashMap<>();

    private final Map<String, BinaryOperator<Double>> DOUBLE_ARGUMENTS_FUNCTIONS = new HashMap<>();

    private final Map<String, Double> CONSTANTS = new HashMap<>();

    private String expression;

    private int location = 0;

    public CalcExpEvaluator() {}

    private boolean isOver() {
        return location >= expression.length();
    }

    private char next() {
        if (isOver()) {
            throw new CalcExpEvalException("文字数を超えた位置へのアクセスが発生しました");
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

    private double number() {
        final char init = next();
        final StringBuilder stringBuilder = new StringBuilder();

        if (SIGNS.contains(init)) {
            stringBuilder.append(init);
            beforeWhitespace();

            if (isOver()) {
                throw new CalcExpEvalException("符号の後には数が必要です");
            }
        }
        else {
            location--;
        }

        final char futureNext = expression.charAt(location);

        if (isZeroArgFunction()) {
            final var function = getZeroArgFunction();
            final var args = arguments();
            if (!args.isEmpty()) throw new CalcExpEvalException("関数の引数の数は0つが要求されています");
            stringBuilder.append(function.getAsDouble());
        }
        else if (isSingleArgFunction()) {
            final var function = getSingleArgFunction();
            final var args = arguments();
            if (args.size() != 1) throw new CalcExpEvalException("関数の引数の数は1つが要求されています");
            stringBuilder.append(function.applyAsDouble(args.getFirst()));
        }
        else if (isDoubleArgsFunction()) {
            final var function = getDoubleArgsFunction();
            final var args = arguments();
            if (args.size() != 2) throw new CalcExpEvalException("関数の引数の数は2つが要求されています");
            stringBuilder.append(function.apply(args.get(0), args.get(1)));
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

                if (INTEGERS.contains(current)) stringBuilder.append(current);
                else if (current == DECIMAL_POINT) {
                    if (dotAlreadyAppended) {
                        throw new CalcExpEvalException("無効な小数点を検知しました");
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
            return Double.parseDouble(stringBuilder.toString());
        }
        catch (NumberFormatException e) {
            throw new CalcExpEvalException("数値の解析に失敗しました: " + expression.substring(location), e);
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
            throw new CalcExpEvalException("無効な符号です: " + sign);
        }
    }

    private double monomial() {
        double value = operateIfSuffix(factor());

        while (!isOver()) {
            final char current = next();
            if (MONOMIAL_OPERATORS.containsKey(current)) {
                value = MONOMIAL_OPERATORS.get(current).apply(value, operateIfSuffix(factor()));
            }
            else {
                location--;
                break;
            }
        }

        return value;
    }

    private double polynomial() {
        double value = monomial();

        while (!isOver()) {
            final char current = next();
            if (POLYNOMIAL_OPERATORS.containsKey(current)) {
                value = POLYNOMIAL_OPERATORS.get(current).apply(value, monomial());
            }
            else {
                location--;
                break;
            }
        }

        return value;
    }

    private double operateIfSuffix(double num) {
        if (!isOver()) {
            final char current2 = next();
            if (NUMBER_SUFFIX_OPERATOR.containsKey(current2)) {
                return NUMBER_SUFFIX_OPERATOR.get(current2).applyAsDouble(num);
            }
            else if (FACTOR_OPERATORS.containsKey(current2)) {
                final double obj = factor();
                return FACTOR_OPERATORS.get(current2).apply(num, obj);
            }
            else {
                location--;
                return num;
            }
        }

        return num;
    }

    private double factor() {
        final char current = next();

        if (current == PARENTHESIS_START) {
            double value = polynomial();
            if (isOver()) throw new CalcExpEvalException("括弧が閉じられていません");
            final char next = next();
            if (next == PARENTHESIS_END) return value;
            else throw new CalcExpEvalException("括弧が閉じられていません: " + next);
        }
        else {
            location--;
            double num = number();

            if (Double.isNaN(num)) {
                throw new CalcExpEvalException("関数または定数からNaNが出力されました");
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
                if (isOver()) throw new CalcExpEvalException("引数の探索中に文字列外に来ました");
                double value = polynomial();
                final char next = next();
                if (next == FUNCTION_ARGUMENT_SEPARATOR) {
                    args.add(value);
                }
                else if (next == PARENTHESIS_END) {
                    args.add(value);
                    return args;
                }
                else throw new CalcExpEvalException("関数の引数の区切りが見つかりません: " + next);
            }
        }
        else throw new CalcExpEvalException("関数の呼び出しには括弧が必要です: " + current);
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

        throw new CalcExpEvalException("定数を取得できませんでした");
    }

    private boolean isZeroArgFunction() {
        final String str = expression.substring(location);

        for (final String name : ZERO_ARGUMENT_FUNCTIONS.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            if (str.startsWith(name)) {
                return true;
            }
        }

        return false;
    }

    private DoubleSupplier getZeroArgFunction() {
        final String str = expression.substring(location);

        for (final String name : ZERO_ARGUMENT_FUNCTIONS.keySet()) {
            if (str.startsWith(name)) {
                location += name.length();
                return ZERO_ARGUMENT_FUNCTIONS.get(name);
            }
        }

        throw new CalcExpEvalException("関数を取得できませんでした");
    }

    private boolean isSingleArgFunction() {
        final String str = expression.substring(location);

        for (final String name : SINGLE_ARGUMENT_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                return true;
            }
        }

        return false;
    }

    private DoubleUnaryOperator getSingleArgFunction() {
        final String str = expression.substring(location);

        for (final String name : SINGLE_ARGUMENT_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                location += name.length();
                return SINGLE_ARGUMENT_FUNCTIONS.get(name);
            }
        }

        throw new CalcExpEvalException("関数を取得できませんでした");
    }

    private boolean isDoubleArgsFunction() {
        final String str = expression.substring(location);

        for (final String name : DOUBLE_ARGUMENTS_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                return true;
            }
        }

        return false;
    }

    private BinaryOperator<Double> getDoubleArgsFunction() {
        final String str = expression.substring(location);

        for (final String name : DOUBLE_ARGUMENTS_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                location += name.length();
                return DOUBLE_ARGUMENTS_FUNCTIONS.get(name);
            }
        }

        throw new CalcExpEvalException("関数を取得できませんでした");
    }

    private void checkExtra() {
        if (!expression.substring(location).isEmpty()) {
            throw new CalcExpEvalException("式の終了後に無効な文字を検出しました: " + expression.substring(location));
        }
    }

    public boolean isDefined(@NotNull String name) {
        return ZERO_ARGUMENT_FUNCTIONS.containsKey(name)
            || SINGLE_ARGUMENT_FUNCTIONS.containsKey(name)
            || DOUBLE_ARGUMENTS_FUNCTIONS.containsKey(name)
            || CONSTANTS.containsKey(name);
    }

    private void throwIfDefined(@NotNull String name) {
        if (isDefined(name)) {
            throw new IllegalArgumentException("その名前は既に使用されています: " + name);
        }
    }

    public double evaluate(@NotNull String expression) throws CalcExpEvalException {
        this.expression = expression;
        if (isOver()) throw new CalcExpEvalException("空文字は計算できません");
        final double value = polynomial();
        checkExtra();
        location = 0;

        if (Double.isNaN(value)) {
            throw new CalcExpEvalException("式からNaNが出力されました");
        }

        return value;
    }

    public void define(@NotNull String name, @NotNull DoubleSupplier function) {
        throwIfDefined(name);
        ZERO_ARGUMENT_FUNCTIONS.put(name, function);
    }

    public void define(@NotNull String name, @NotNull DoubleUnaryOperator function) {
        throwIfDefined(name);
        SINGLE_ARGUMENT_FUNCTIONS.put(name, function);
    }

    public void define(@NotNull String name, @NotNull BinaryOperator<Double> function) {
        throwIfDefined(name);
        DOUBLE_ARGUMENTS_FUNCTIONS.put(name, function);
    }

    public void define(@NotNull String name, double constant) {
        throwIfDefined(name);
        CONSTANTS.put(name, constant);
    }

    public <T> void define(@NotNull String name, @NotNull Class<T> clazz, @Nullable T object) {
        final Predicate<Class<?>> classChecker = (__clazz__) -> __clazz__.equals(Double.class) || __clazz__.equals(double.class);

        for (final Field field : clazz.getFields()) {
            try {
                field.setAccessible(true);
                final Object value = field.get(object);
                if (value instanceof Number number) {
                    define(name + "." + field.getName(), number.doubleValue());
                }
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException("NEVER HAPPENS");
            }
        }

        for (final Method method : clazz.getMethods()) {
            if (!classChecker.test(method.getReturnType())) continue;

            final String id = name + "." + method.getName();

            method.setAccessible(true);

            if (isDefined(id)) undefine(id);

            switch (method.getParameterCount()) {
                case 0:
                    define(id, () -> {
                        try {
                            return (double) method.invoke(object);
                        }
                        catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("NEVER HAPPENS");
                        }
                    });
                    break;
                case 1:
                    if (!classChecker.test(method.getParameterTypes()[0])) continue;

                    define(id, x -> {
                        try {
                            return (double) method.invoke(object, x);
                        }
                        catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("NEVER HAPPENS");
                        }
                    });

                    break;
                case 2:
                    if (!classChecker.test(method.getParameterTypes()[0]) || !classChecker.test(method.getParameterTypes()[1])) {
                        continue;
                    }

                    define(id, (x, y) -> {
                        try {
                            return (double) method.invoke(object, x, y);
                        }
                        catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("NEVER HAPPENS");
                        }
                    });

                    break;
                default:
                    break;
            }
        }
    }

    public void undefine(@NotNull String name) {
        if (isDefined(name)) {
            ZERO_ARGUMENT_FUNCTIONS.remove(name);
            SINGLE_ARGUMENT_FUNCTIONS.remove(name);
            DOUBLE_ARGUMENTS_FUNCTIONS.remove(name);
            CONSTANTS.remove(name);
        }
    }

    @Override
    public @NotNull String toString() {
        return "<CalcExpEvaluator>";
    }

    public static @NotNull CalcExpEvaluator getDefaultEvaluator() {
        final CalcExpEvaluator evaluator = new CalcExpEvaluator();

        evaluator.define("NaN", Double.NaN);
        evaluator.define("PI", Math.PI);
        evaluator.define("TAU", Math.TAU);
        evaluator.define("E", Math.E);
        evaluator.define("Infinity", Double.POSITIVE_INFINITY);

        evaluator.define("random", Math::random);

        evaluator.define("sqrt", Math::sqrt);
        evaluator.define("cbrt", Math::cbrt);
        evaluator.define("abs", Math::abs);
        evaluator.define("floor", Math::floor);
        evaluator.define("ceil", Math::ceil);
        evaluator.define("round", Math::round);
        evaluator.define("sin", Math::sin);
        evaluator.define("cos", Math::cos);
        evaluator.define("tan", Math::tan);
        evaluator.define("asin", Math::asin);
        evaluator.define("acos", Math::acos);
        evaluator.define("atan", Math::atan);
        evaluator.define("exp", Math::exp);
        evaluator.define("to_degrees", Math::toDegrees);
        evaluator.define("to_radians", Math::toRadians);
        evaluator.define("log10", Math::log10);

        evaluator.define("log", (a, b) -> Math.log(b) / Math.log(a));
        evaluator.define("atan2", Math::atan2);
        evaluator.define("min", Math::min);
        evaluator.define("max", Math::max);
        evaluator.define("pow", Math::pow);

        return evaluator;
    }
}
