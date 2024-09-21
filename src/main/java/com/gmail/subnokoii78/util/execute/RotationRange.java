package com.gmail.subnokoii78.util.execute;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 正のfloat型の値の範囲を表現するクラス
 */
public final class RotationRange {
    private final float min;

    private final float max;

    private RotationRange(@Nullable Float min, @Nullable Float max) {
        if (min == null) this.min = 0f;
        else this.min = min;
        if (max == null) this.max = Float.POSITIVE_INFINITY;
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
     * 新しく向きの範囲を作成します。
     * @param input セレクター引数x|y_rotation=に渡す文字列
     * @return {@link RotationRange}
     * @throws IllegalArgumentException 文字列の形式が正しくないとき
     */
    public static @NotNull RotationRange of(@NotNull @Pattern("(?:^\\d+(?:\\.\\d+)?$)|(?:^\\d+(?:\\.\\d+)?\\.\\.$)|(?:^\\.\\.\\d+(?:\\.\\d+)?$)|(?:^\\d+(?:\\.\\d+)?\\.\\.\\d+(?:\\.\\d+)?$)") String input) throws IllegalArgumentException {
        if (input.matches("^\\d+(?:\\.\\d+)?$")) {
            final float value = Float.parseFloat(input);
            return new RotationRange(value, value);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.$")) {
            final float min = Float.parseFloat(input.substring(0, input.length() - 2));
            return new RotationRange(min, null);
        }
        else if (input.matches("^\\.\\.\\d+(?:\\.\\d+)?$")) {
            final float max = Float.parseFloat(input.substring(2));
            return new RotationRange(null, max);
        }
        else if (input.matches("^\\d+(?:\\.\\d+)?\\.\\.\\d+(?:\\.\\d+)?$")) {
            final String[] separated = input.split("\\.\\.");
            if (separated.length > 2) throw new IllegalArgumentException("NEVER HAPPENS");
            final float min = Float.parseFloat(separated[0]);
            final float max = Float.parseFloat(separated[1]);
            return new RotationRange(min, max);
        }
        else throw new IllegalArgumentException("無効な文字列です");
    }
}
