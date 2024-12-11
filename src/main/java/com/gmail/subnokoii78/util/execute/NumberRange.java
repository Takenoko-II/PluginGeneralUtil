package com.gmail.subnokoii78.util.execute;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NumberRange<T extends Number> {
    private final T min;

    private final T max;

    private NumberRange(@NotNull T value1, @NotNull T value2) {
        if (value1.doubleValue() >= value2.doubleValue()) {
            min = value2;
            max = value1;
        }
        else {
            min = value1;
            max = value2;
        }
    }

    public @NotNull T min() {
        return min;
    }

    public @NotNull T max() {
        return max;
    }

    public boolean within(byte value) {
        return min.byteValue() <= value && value <= max.byteValue();
    }

    public boolean within(short value) {
        return min.shortValue() <= value && value <= max.shortValue();
    }

    public boolean within(int value) {
        return min.intValue() <= value && value <= max.intValue();
    }

    public boolean within(long value) {
        return min.longValue() <= value && value <= max.longValue();
    }

    public boolean within(float value) {
        return min.floatValue() <= value && value <= max.floatValue();
    }

    public boolean within(double value) {
        return min.doubleValue() <= value && value <= max.doubleValue();
    }

    private static final class Parser<T extends Number> {
        private static final String SIGN = "[+-]?";

        private final String NUMBER_PATTERN;

        private final T POSITIVE_INFINITY;

        private final T NEGATIVE_INFINITY;

        private final Function<String, T> parser;

        private Parser(@NotNull T defaultMin, @NotNull T defaultMax, @NotNull String pattern, @NotNull Function<String, T> parser) {
            this.POSITIVE_INFINITY = defaultMin;
            this.NEGATIVE_INFINITY = defaultMax;
            this.NUMBER_PATTERN = pattern;
            this.parser = parser;
        }

        private @NotNull NumberRange<T> singleValue(@NotNull String input) {
            final T value = parser.apply(input);
            return new NumberRange<>(value, value);
        }

        private @NotNull NumberRange<T> minOnly(@NotNull String input) {
            final T value = parser.apply(input.substring(0, input.length() - 2));
            return new NumberRange<>(value, POSITIVE_INFINITY);
        }

        private @NotNull NumberRange<T> maxOnly(@NotNull String input) {
            final T value = parser.apply(input.substring(2));
            return new NumberRange<>(NEGATIVE_INFINITY, value);
        }

        private @NotNull NumberRange<T> minMax(@NotNull String input) {
            final String[] separated = input.split("\\.\\.");
            final T min = parser.apply(separated[0]);
            final T max = parser.apply(separated[1]);
            if (min.doubleValue() > max.doubleValue()) {
                throw new IllegalArgumentException("最大値よりも大きい最小値は無効です");
            }
            return new NumberRange<>(min, max);
        }

        private @NotNull NumberRange<T> parse(@NotNull String input, boolean allowSign) {
            final String pattern = (allowSign) ? SIGN + NUMBER_PATTERN : NUMBER_PATTERN;

            if (input.matches("^" + pattern + "$")) {
                return singleValue(input);
            }
            else if (input.matches("^" + pattern + "\\.\\.$")) {
                return minOnly(input);
            }
            else if (input.matches("^\\.\\." + pattern + "$")) {
                return maxOnly(input);
            }
            else if (input.matches("^" + pattern + "\\.\\." + pattern + "$")) {
                return minMax(input);
            }
            else throw new IllegalArgumentException("無効な文字列です");
        }
    }

    @RegExp
    private static final String INTEGER_PATTERN = "\\d+";

    @RegExp
    private static final String DECIMAL_PATTERN = "(?:\\d+\\.?\\d*|\\.\\d+)";

    private static final Parser<Byte> BYTE_PARSER = new Parser<>(
        Byte.MIN_VALUE,
        Byte.MAX_VALUE,
        INTEGER_PATTERN,
        Byte::parseByte
    );

    private static final Parser<Short> SHORT_PARSER = new Parser<>(
        Short.MIN_VALUE,
        Short.MAX_VALUE,
        INTEGER_PATTERN,
        Short::parseShort
    );

    private static final Parser<Integer> INTEGER_PARSER = new Parser<>(
        Integer.MIN_VALUE,
        Integer.MAX_VALUE,
        INTEGER_PATTERN,
        Integer::parseInt
    );

    private static final Parser<Long> LONG_PARSER = new Parser<>(
        Long.MIN_VALUE,
        Long.MAX_VALUE,
        INTEGER_PATTERN,
        Long::parseLong
    );

    private static final Parser<Float> FLOAT_PARSER = new Parser<>(
        Float.POSITIVE_INFINITY,
        Float.NEGATIVE_INFINITY,
        DECIMAL_PATTERN,
        Float::parseFloat
    );

    private static final Parser<Double> DOUBLE_PARSER = new Parser<>(
        Double.POSITIVE_INFINITY,
        Double.NEGATIVE_INFINITY,
        DECIMAL_PATTERN,
        Double::parseDouble
    );

    public static final class LevelRange extends NumberRange<Integer> {
        private LevelRange(@NotNull NumberRange<Integer> range) {
            super(range.min, range.max);
        }
    }

    public static final class ScoreRange extends NumberRange<Integer> {
        private ScoreRange(@NotNull NumberRange<Integer> range) {
            super(range.min, range.max);
        }
    }

    public static final class RotationRange extends NumberRange<Float> {
        private RotationRange(@NotNull NumberRange<Float> range) {
            super(range.min, range.max);
        }
    }

    public static final class DistanceRange extends NumberRange<Double> {
        private DistanceRange(@NotNull NumberRange<Double> range) {
            super(range.min, range.max);
        }
    }

    public static @NotNull LevelRange level(@NotNull String input) {
        return new LevelRange(INTEGER_PARSER.parse(input, false));
    }

    public static @NotNull ScoreRange score(@NotNull String input) {
        return new ScoreRange(INTEGER_PARSER.parse(input, true));
    }

    public static @NotNull RotationRange rotation(@NotNull String input) {
        return new RotationRange(FLOAT_PARSER.parse(input, true));
    }

    public static @NotNull DistanceRange distance(@NotNull String input) {
        return new DistanceRange(DOUBLE_PARSER.parse(input, false));
    }
}
