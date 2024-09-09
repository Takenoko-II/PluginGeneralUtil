package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PositiveFloatRange {
    private final float min;

    private final float max;

    private PositiveFloatRange(@Nullable Float min, @Nullable Float max) {
        if (min == null) this.min = 0f;
        else this.min = min;
        if (max == null) this.max = Float.POSITIVE_INFINITY;
        else this.max = max;

        if (this.min > this.max) throw new IllegalArgumentException("min > max");
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public static @NotNull PositiveFloatRange parse(@NotNull String input) throws IllegalArgumentException {
        if (input.matches("^\\d+(?:\\.\\d+)?$")) {
            final float value = Float.parseFloat(input);
            return new PositiveFloatRange(value, value);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.$")) {
            final float min = Float.parseFloat(input.substring(0, input.length() - 2));
            return new PositiveFloatRange(min, null);
        }
        else if (input.matches("^\\.\\.\\d+(?:\\.\\d+)?$")) {
            final float max = Float.parseFloat(input.substring(2));
            return new PositiveFloatRange(null, max);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.\\d+(?:\\.\\d+)?$")) {
            final String[] separated = input.split("\\.\\.");
            if (separated.length > 2) throw new IllegalArgumentException("NEVER HAPPENS");
            final float min = Float.parseFloat(separated[0]);
            final float max = Float.parseFloat(separated[1]);
            return new PositiveFloatRange(min, max);
        }
        else throw new IllegalArgumentException("無効な文字列です");
    }
}
