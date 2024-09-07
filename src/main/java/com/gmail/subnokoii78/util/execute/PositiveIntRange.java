package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PositiveIntRange {
    private final int min;

    private final int max;

    private PositiveIntRange(@Nullable Integer min, @Nullable Integer max) {
        if (min == null) this.min = 0;
        else this.min = min;
        if (max == null) this.max = Integer.MAX_VALUE;
        else this.max = max;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public static @NotNull PositiveIntRange parse(@NotNull String input) throws IllegalArgumentException {
        if (input.matches("^\\d+$")) {
            final int value = Integer.parseInt(input);
            return new PositiveIntRange(value, value);
        }
        else if (input.matches("^\\d+\\.\\.$")) {
            final int min = Integer.parseInt(input.substring(0, input.length() - 2));
            return new PositiveIntRange(min, null);
        }
        else if (input.matches("^\\.\\.\\d+$")) {
            final int max = Integer.parseInt(input.substring(2));
            return new PositiveIntRange(null, max);
        }
        else if (input.matches("^\\d+\\.\\.\\d+$")) {
            final String[] separated = input.split("\\.\\.");
            if (separated.length > 2) throw new IllegalArgumentException("NEVER HAPPENS");
            final int min = Integer.parseInt(separated[0]);
            final int max = Integer.parseInt(separated[1]);
            return new PositiveIntRange(min, max);
        }
        else throw new IllegalArgumentException("無効な文字列です");
    }
}
