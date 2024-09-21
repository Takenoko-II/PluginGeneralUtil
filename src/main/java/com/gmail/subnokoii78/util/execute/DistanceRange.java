package com.gmail.subnokoii78.util.execute;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 正のdouble型の値の範囲を表現するクラス
 */
public final class DistanceRange {
    private final double min;

    private final double max;

    private DistanceRange(@Nullable Double min, @Nullable Double max) {
        if (min == null) this.min = 0d;
        else this.min = min;
        if (max == null) this.max = Double.POSITIVE_INFINITY;
        else this.max = max;

        if (this.min > this.max) throw new IllegalArgumentException("min > max");
    }

    /**
     * 範囲の最小値を返します。
     * @return 最小値
     */
    public double min() {
        return min;
    }

    /**
     * 範囲の最大値を返します。
     * @return 最大値
     */
    public double max() {
        return max;
    }

    /**
     * 新しく距離の範囲を作成します。
     * @param input セレクター引数distance=に渡す文字列
     * @return {@link DistanceRange}
     * @throws IllegalArgumentException 文字列の形式が正しくないとき
     */
    public static @NotNull DistanceRange of(@NotNull @Pattern("(?:^\\d+(?:\\.\\d+)?$)|(?:^\\d+(?:\\.\\d+)?\\.\\.$)|(?:^\\.\\.\\d+(?:\\.\\d+)?$)|(?:^\\d+(?:\\.\\d+)?\\.\\.\\d+(?:\\.\\d+)?$)") String input) throws IllegalArgumentException {
        if (input.matches("^\\d+(?:\\.\\d+)?$")) {
            final double value = Double.parseDouble(input);
            return new DistanceRange(value, value);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.$")) {
            final double min = Double.parseDouble(input.substring(0, input.length() - 2));
            return new DistanceRange(min, null);
        }
        else if (input.matches("^\\.\\.\\d+(?:\\.\\d+)?$")) {
            final double max = Double.parseDouble(input.substring(2));
            return new DistanceRange(null, max);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.\\d+(?:\\.\\d+)?$")) {
            final String[] separated = input.split("\\.\\.");
            if (separated.length > 2) throw new IllegalArgumentException("NEVER HAPPENS");
            final double min = Double.parseDouble(separated[0]);
            final double max = Double.parseDouble(separated[1]);
            return new DistanceRange(min, max);
        }
        else throw new IllegalArgumentException("無効な文字列です");
    }
}
