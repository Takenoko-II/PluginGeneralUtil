package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class CalcExpEvaluator {
    private static final Set<Character> IGNORED = Set.of(' ', '\n');

    private static final List<Character> SIGNS = List.of('+', '-');

    private static final Set<Character> NUMBERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final Function<String, Double> NUMBER_PARSER = Double::parseDouble;

    private static final char DECIMAL_POINT = '.';

    private static final char FUNCTION_ARGUMENT_SEPARATOR = ',';

    private static final char PARENTHESIS_START = '(';

    private static final char PARENTHESIS_END = ')';

    private static void throwIfInvalidChar(@NotNull String word) {
        if (word.contains(String.valueOf(PARENTHESIS_START))
            || word.contains(String.valueOf(PARENTHESIS_END))
            || word.contains(String.valueOf(FUNCTION_ARGUMENT_SEPARATOR)))
        {
            throw new IllegalArgumentException(String.format(
                "次の文字を含む名前は使用できません: ['%c'], ['%c'], ['%c']",
                PARENTHESIS_START,
                PARENTHESIS_END,
                FUNCTION_ARGUMENT_SEPARATOR
            ));
        }
    }

    private final Map<String, DoubleBinaryOperator> MONOMIAL_OPERATORS = new HashMap<>(Map.of(
        "*", (a, b) -> a * b,
        "/", (a, b) -> {
            if (a == 0 && b == 0) {
                location = 0;
                throw new CalcExpEvaluationException("式 '0 / 0'はNaNを返します");
            }

            return a / b;
        },
        "%", (a, b) -> {
            if (a == 0 && b == 0) {
                location = 0;
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

    private final Map<String, DoubleUnaryOperator> NUMBER_SUFFIX_OPERATORS = new HashMap<>(Map.of(
        "!", value -> {
            if (value != (double) (int) value) {
                location = 0;
                throw new CalcExpEvaluationException("階乗演算子は実質的な整数の値にのみ使用できます");
            }
            else if (value < 0) {
                location = 0;
                throw new CalcExpEvaluationException("階乗演算子は負の値に使用できません");
            }
            else if (value > 127) {
                location = 0;
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
            location = 0;
            throw new CalcExpEvaluationException("文字数を超えた位置へのアクセスが発生しました");
        }

        final char current = expression.charAt(location++);

        if (IGNORED.contains(current)) return next();

        return current;
    }

    private void ignore() {
        if (isOver()) return;

        final char current = expression.charAt(location++);

        if (IGNORED.contains(current)) {
            ignore();
        }
        else {
            location--;
        }
    }

    private boolean nextIf(@NotNull String next) {
        if (isOver()) return false;

        ignore();

        final String str = expression.substring(location);

        if (str.startsWith(next)) {
            location += next.length();
            ignore();
            return true;
        }

        return false;
    }

    private boolean ignoringStartsWith(@NotNull String text) {
        ignore();
        return expression.substring(location).startsWith(text);
    }

    private double number() {
        final char init = next();
        final StringBuilder stringBuilder = new StringBuilder();

        if (SIGNS.contains(init)) {
            stringBuilder.append(init);
            ignore();

            if (isOver()) {
                location = 0;
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
                        location = 0;
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
            location = 0;
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
            location = 0;
            throw new CalcExpEvaluationException("無効な符号です: " + sign);
        }
    }

    private double monomial() {
        double value = factorOperator(factor());

        a: while (!isOver()) {
            for (final String o : MONOMIAL_OPERATORS.keySet()) {
                if (nextIf(o)) {
                    value = MONOMIAL_OPERATORS.get(o).applyAsDouble(value, factorOperator(factor()));
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

    private double factorOperator(double num) {
        double value = num;

        a: while (!isOver()) {
            for (final String o : NUMBER_SUFFIX_OPERATORS.keySet()) {
                if (nextIf(o)) {
                    value = NUMBER_SUFFIX_OPERATORS.get(o).applyAsDouble(value);
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
            if (isOver()) {
                location = 0;
                throw new CalcExpEvaluationException("括弧が閉じられていません");
            }
            final char next = next();
            if (next == PARENTHESIS_END) {
                ignore();
                return value;
            }
            else {
                location = 0;
                throw new CalcExpEvaluationException("括弧が閉じられていません: " + next);
            }
        }
        else {
            location--;
            double num = number();

            if (Double.isNaN(num)) {
                location = 0;
                throw new CalcExpEvaluationException("関数または定数からNaNが出力されました");
            }
            return num;
        }
    }

    private List<Double> arguments() {
        final List<Double> args = new ArrayList<>();

        if (!nextIf(String.valueOf(PARENTHESIS_START))) {
            location = 0;
            throw new CalcExpEvaluationException("関数の呼び出しには括弧が必要です");
        }

        if (nextIf(String.valueOf(PARENTHESIS_END))) {
            return args;
        }

        while (true) {
            if (isOver()) {
                location = 0;
                throw new CalcExpEvaluationException("引数の探索中に文字列外に来ました");
            }
            double value = polynomial();
            final char next = next();
            if (next == FUNCTION_ARGUMENT_SEPARATOR) {
                args.add(value);
            }
            else if (next == PARENTHESIS_END) {
                args.add(value);
                ignore();
                return args;
            }
            else {
                location = 0;
                throw new CalcExpEvaluationException("関数の引数の区切りが見つかりません: " + next);
            }
        }
    }

    private boolean isConst() {
        for (final String name : CONSTANTS.keySet()) {
            if (ignoringStartsWith(name)) {
                return true;
            }
        }

        return false;
    }

    private Double getConst() {
        for (final String name : CONSTANTS.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            if (nextIf(name)) {
                return CONSTANTS.get(name);
            }
        }

        location = 0;
        throw new CalcExpEvaluationException("定数を取得できませんでした");
    }

    private boolean isFunction() {
        for (final String name : FUNCTIONS.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            final int loc = location;
            if (nextIf(name) && nextIf(String.valueOf(PARENTHESIS_START))) {
                location = loc;
                return true;
            }
            location = loc;
        }

        return false;
    }

    private Function<List<Double>, Double> getFunction() {
        for (final String name : FUNCTIONS.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            if (nextIf(name) && ignoringStartsWith(String.valueOf(PARENTHESIS_START))) {
                return FUNCTIONS.get(name);
            }
        }

        location = 0;
        throw new CalcExpEvaluationException("関数を取得できませんでした");
    }

    private double index() {
        if (location != 0) {
            location = 0;
            throw new CalcExpEvaluationException("カーソル位置が0ではありませんでした: インスタンス自身がevaluate()を呼び出した可能性があります");
        }

        if (isOver()) {
            location = 0;
            throw new CalcExpEvaluationException("空文字は計算できません");
        }

        final double value = polynomial();

        if (!expression.substring(location).isEmpty()) {
            location = 0;
            throw new CalcExpEvaluationException("式の終了後に無効な文字を検出しました: " + expression.substring(location));
        }

        return value;
    }

    public double evaluate(@NotNull String expression) throws CalcExpEvaluationException {
        this.expression = expression;

        final double value = index();

        location = 0;

        if (Double.isNaN(value)) {
            throw new CalcExpEvaluationException("式からNaNが出力されました");
        }

        return value;
    }

    public boolean isDeclared(@NotNull String name, @NotNull DeclarationCategory category) {
        return switch (category) {
            case CONSTANT -> CONSTANTS.containsKey(name);
            case FUNCTION -> FUNCTIONS.containsKey(name);
            case OPERATOR -> POLYNOMIAL_OPERATORS.containsKey(name)
                || MONOMIAL_OPERATORS.containsKey(name)
                || FACTOR_OPERATORS.containsKey(name);
            case SELF_OPERATOR -> NUMBER_SUFFIX_OPERATORS.containsKey(name);
        };
    }

    public <T> void declare(@NotNull String name, @NotNull DeclarationKey<T> key, @NotNull T value) {
        throwIfInvalidChar(name);

        switch (key.getCategory()) {
            case CONSTANT -> CONSTANTS.put(name, key.constant(value));
            case FUNCTION -> FUNCTIONS.put(name, key.function(value));
            case OPERATOR -> {
                final Map<String, DoubleBinaryOperator> map;
                if (key == DeclarationKey.OPERATOR_POLYNOMIAL) map = POLYNOMIAL_OPERATORS;
                else if (key == DeclarationKey.OPERATOR_MONOMIAL) map = MONOMIAL_OPERATORS;
                else if (key == DeclarationKey.OPERATOR_FACTOR) map = FACTOR_OPERATORS;
                else throw new IllegalArgumentException("不明な演算子定義タイプです");
                map.put(name, key.operator(value));
            }
            case SELF_OPERATOR -> {
                final Map<String, DoubleUnaryOperator> map;
                if (key == DeclarationKey.SELF_OPERATOR_NUMBER_SUFFIX) map = NUMBER_SUFFIX_OPERATORS;
                else throw new IllegalArgumentException("不明な演算子定義タイプです");
                map.put(name, key.selfOperator(value));
            }
        }
    }

    public void undeclare(@NotNull String name, @NotNull DeclarationCategory category) {
        if (isDeclared(name, category)) {
            switch (category) {
                case CONSTANT -> CONSTANTS.remove(name);
                case FUNCTION -> FUNCTIONS.remove(name);
                case OPERATOR -> {
                    POLYNOMIAL_OPERATORS.remove(name);
                    MONOMIAL_OPERATORS.remove(name);
                    FACTOR_OPERATORS.remove(name);
                }
                case SELF_OPERATOR -> NUMBER_SUFFIX_OPERATORS.remove(name);
            }
        }
    }

    public @NotNull Set<String> getDeclarationNames(@NotNull DeclarationCategory category) {
        return switch (category) {
            case CONSTANT -> Set.copyOf(CONSTANTS.keySet());
            case FUNCTION -> Set.copyOf(FUNCTIONS.keySet());
            case OPERATOR -> {
                final Set<String> set = new HashSet<>(Set.copyOf(POLYNOMIAL_OPERATORS.keySet()));
                set.addAll(MONOMIAL_OPERATORS.keySet());
                set.addAll(FACTOR_OPERATORS.keySet());
                yield Set.copyOf(set);
            }
            case SELF_OPERATOR -> Set.copyOf(NUMBER_SUFFIX_OPERATORS.keySet());
        };
    }

    @Override
    public @NotNull String toString() {
        return "<CalcExpEvaluator>";
    }

    public static @NotNull CalcExpEvaluator getDefaultEvaluator() {
        final CalcExpEvaluator evaluator = new CalcExpEvaluator();

        evaluator.declare("NaN", DeclarationKey.CONSTANT, Double.NaN);
        evaluator.declare("PI", DeclarationKey.CONSTANT, Math.PI);
        evaluator.declare("TAU", DeclarationKey.CONSTANT, Math.TAU);
        evaluator.declare("E", DeclarationKey.CONSTANT, Math.E);
        evaluator.declare("Infinity", DeclarationKey.CONSTANT, Double.POSITIVE_INFINITY);

        evaluator.declare("random", DeclarationKey.FUNCTION_NO_ARGS, Math::random);

        evaluator.declare("sqrt", DeclarationKey.FUNCTION_1_ARG, Math::sqrt);
        evaluator.declare("cbrt", DeclarationKey.FUNCTION_1_ARG, Math::cbrt);
        evaluator.declare("abs", DeclarationKey.FUNCTION_1_ARG, Math::abs);
        evaluator.declare("floor", DeclarationKey.FUNCTION_1_ARG, Math::floor);
        evaluator.declare("ceil", DeclarationKey.FUNCTION_1_ARG, Math::ceil);
        evaluator.declare("round", DeclarationKey.FUNCTION_1_ARG, Math::round);
        evaluator.declare("sin", DeclarationKey.FUNCTION_1_ARG, Math::sin);
        evaluator.declare("cos", DeclarationKey.FUNCTION_1_ARG, Math::cos);
        evaluator.declare("tan", DeclarationKey.FUNCTION_1_ARG, Math::tan);
        evaluator.declare("asin", DeclarationKey.FUNCTION_1_ARG, Math::asin);
        evaluator.declare("acos", DeclarationKey.FUNCTION_1_ARG, Math::acos);
        evaluator.declare("atan", DeclarationKey.FUNCTION_1_ARG, Math::atan);
        evaluator.declare("exp", DeclarationKey.FUNCTION_1_ARG, Math::exp);
        evaluator.declare("to_degrees", DeclarationKey.FUNCTION_1_ARG, Math::toDegrees);
        evaluator.declare("to_radians", DeclarationKey.FUNCTION_1_ARG, Math::toRadians);
        evaluator.declare("log10", DeclarationKey.FUNCTION_1_ARG, Math::log10);

        evaluator.declare("log", DeclarationKey.FUNCTION_2_ARGS, (a, b) -> Math.log(b) / Math.log(a));
        evaluator.declare("atan2", DeclarationKey.FUNCTION_2_ARGS, Math::atan2);
        evaluator.declare("min", DeclarationKey.FUNCTION_2_ARGS, Math::min);
        evaluator.declare("max", DeclarationKey.FUNCTION_2_ARGS, Math::max);
        evaluator.declare("pow", DeclarationKey.FUNCTION_2_ARGS, Math::pow);

        return evaluator;
    }
}
