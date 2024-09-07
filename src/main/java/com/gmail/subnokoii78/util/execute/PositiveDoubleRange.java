package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PositiveDoubleRange {
    private final double min;

    private final double max;

    private PositiveDoubleRange(@Nullable Double min, @Nullable Double max) {
        if (min == null) this.min = 0d;
        else this.min = min;
        if (max == null) this.max = Double.POSITIVE_INFINITY;
        else this.max = max;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public static @NotNull PositiveDoubleRange parse(@NotNull String input) throws IllegalArgumentException {
        if (input.matches("^\\d+(?:\\.\\d+)?$")) {
            final double value = Double.parseDouble(input);
            return new PositiveDoubleRange(value, value);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.$")) {
            final double min = Double.parseDouble(input.substring(0, input.length() - 2));
            return new PositiveDoubleRange(min, null);
        }
        else if (input.matches("^\\.\\.\\d+(?:\\.\\d+)?$")) {
            final double max = Double.parseDouble(input.substring(2));
            return new PositiveDoubleRange(null, max);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.\\d+(?:\\.\\d+)?$")) {
            final String[] separated = input.split("\\.\\.");
            if (separated.length > 2) throw new IllegalArgumentException("NEVER HAPPENS");
            final double min = Double.parseDouble(separated[0]);
            final double max = Double.parseDouble(separated[1]);
            return new PositiveDoubleRange(min, max);
        }
        else throw new IllegalArgumentException("無効な文字列です");
    }
}
