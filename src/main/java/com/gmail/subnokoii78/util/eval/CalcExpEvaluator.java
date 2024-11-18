package com.gmail.subnokoii78.util.eval;

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

    private static final Set<Character> NUMBERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final Function<String, Double> NUMBER_PARSER = Double::parseDouble;

    private static final char DECIMAL_POINT = '.';

    private static final char FUNCTION_ARGUMENT_SEPARATOR = ',';

    private static final char PARENTHESIS_START = '(';

    private static final char PARENTHESIS_END = ')';

    private final Map<String, BinaryOperator<Double>> MONOMIAL_OPERATORS = new HashMap<>(Map.of(
        "*", (a, b) -> a * b,
        "/", (a, b) -> {
            if (a == 0 && b == 0) {
                throw new CalcExpEvalException("式 '0 / 0'はNaNを返します");
            }

            return a / b;
        },
        "%", (a, b) -> {
            if (a == 0 && b == 0) {
                throw new CalcExpEvalException("式 '0 % 0'はNaNを返します");
            }

            return a % b;
        }
    ));

    private final Map<String, BinaryOperator<Double>> POLYNOMIAL_OPERATORS = new HashMap<>(Map.of(
        "+", Double::sum,
        "-", (a, b) -> a - b
    ));

    private final Map<String, BinaryOperator<Double>> FACTOR_OPERATORS = new HashMap<>(Map.of(
        "^", Math::pow
    ));

    private final Map<String, DoubleUnaryOperator> NUMBER_SUFFIX_OPERATOR = new HashMap<>(Map.of(
        "!", value -> {
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

    private final Map<String, CalcExpFunction> FUNCTIONS = new HashMap<>();

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
                throw new CalcExpEvalException("符号の後には数が必要です");
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
            return NUMBER_PARSER.apply(stringBuilder.toString());
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

        a: while (!isOver()) {
            for (final String o : MONOMIAL_OPERATORS.keySet()) {
                if (nextIf(o)) {
                    value = MONOMIAL_OPERATORS.get(o).apply(value, operateIfSuffix(factor()));
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
                    value = POLYNOMIAL_OPERATORS.get(o).apply(value, monomial());
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
                    value = FACTOR_OPERATORS.get(o).apply(value, obj);
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
            if (isOver()) throw new CalcExpEvalException("括弧が閉じられていません");
            final char next = next();
            if (next == PARENTHESIS_END) {
                beforeWhitespace();
                return value;
            }
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

        throw new CalcExpEvalException("関数を取得できませんでした");
    }

    private void checkExtra() {
        if (!expression.substring(location).isEmpty()) {
            throw new CalcExpEvalException("式の終了後に無効な文字を検出しました: " + expression.substring(location));
        }
    }

    public boolean isDefined(@NotNull String name) {
        return FUNCTIONS.containsKey(name) || CONSTANTS.containsKey(name);
    }

    private void throwIfDefined(@NotNull String name) throws IllegalArgumentException {
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

    public void define(@NotNull String name, double constant) {
        throwIfDefined(name);
        CONSTANTS.put(name, constant);
    }

    public void define(@NotNull String name, @NotNull CalcExpFunction function) {
        throwIfDefined(name);
        FUNCTIONS.put(name, function);
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

            define(id, list -> {
                try {
                    return (double) method.invoke(object, list.toArray());
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("NEVER HAPPENS");
                }
            });
        }
    }

    public void undefine(@NotNull String name) {
        if (isDefined(name)) {
            FUNCTIONS.remove(name);
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

        evaluator.define("random", CalcExpFunction.of(Math::random));

        evaluator.define("sqrt", CalcExpFunction.of(Math::sqrt));
        evaluator.define("cbrt", CalcExpFunction.of(Math::cbrt));
        evaluator.define("abs", CalcExpFunction.of(Math::abs));
        evaluator.define("floor", CalcExpFunction.of(Math::floor));
        evaluator.define("ceil", CalcExpFunction.of(Math::ceil));
        evaluator.define("round", CalcExpFunction.of(Math::round));
        evaluator.define("sin", CalcExpFunction.of(Math::sin));
        evaluator.define("cos", CalcExpFunction.of(Math::cos));
        evaluator.define("tan", CalcExpFunction.of(Math::tan));
        evaluator.define("asin", CalcExpFunction.of(Math::asin));
        evaluator.define("acos", CalcExpFunction.of(Math::acos));
        evaluator.define("atan", CalcExpFunction.of(Math::atan));
        evaluator.define("exp", CalcExpFunction.of(Math::exp));
        evaluator.define("to_degrees", CalcExpFunction.of(Math::toDegrees));
        evaluator.define("to_radians", CalcExpFunction.of(Math::toRadians));
        evaluator.define("log10", CalcExpFunction.of(Math::log10));

        evaluator.define("log", CalcExpFunction.of((a, b) -> Math.log(b) / Math.log(a)));
        evaluator.define("atan2", CalcExpFunction.of(Math::atan2));
        evaluator.define("min", CalcExpFunction.of(Math::min));
        evaluator.define("max", CalcExpFunction.of(Math::max));
        evaluator.define("pow", CalcExpFunction.of(Math::pow));

        class Bit {
            public static double and(double a, double b) {
                return (int) a & (int) b;
            }

            public static double or(double a, double b) {
                return (int) a | (int) b;
            }

            public static double xor(double a, double b) {
                return (int) a ^ (int) b;
            }

            public static double not(double x) {
                return ~ (int) x;
            }

            public static double shift_left(double a, double b) {
                return (int) a << (int) b;
            }

            public static double shift_right(double a, double b) {
                return (int) a >> (int) b;
            }

            public static double shift_right_unsigned(double a, double b) {
                return (int) a >>> (int) b;
            }
        }

        evaluator.define("Bit", Bit.class, null);

        return evaluator;
    }
}
