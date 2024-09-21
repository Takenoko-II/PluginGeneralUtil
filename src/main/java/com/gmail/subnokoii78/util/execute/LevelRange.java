package com.gmail.subnokoii78.util.execute;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 正のint型の値の範囲を表現するクラス
 */
public final class LevelRange {
    private final int min;

    private final int max;

    private LevelRange(@Nullable Integer min, @Nullable Integer max) {
        if (min == null) this.min = 0;
        else this.min = min;
        if (max == null) this.max = Integer.MAX_VALUE;
        else this.max = max;

        if (this.min > this.max) throw new IllegalArgumentException("min > max");
    }

    /**
     * 範囲の最小値を返します。
     * @return 最小値
     */
    public int min() {
        return min;
    }

    /**
     * 範囲の最大値を返します。
     * @return 最大値
     */
    public int max() {
        return max;
    }

    /**
     * 新しくレベルの範囲を作成します。
     * @param input セレクター引数level=に渡す文字列
     * @return {@link LevelRange}
     * @throws IllegalArgumentException 文字列の形式が正しくないとき
     */
    public static @NotNull LevelRange of(@NotNull @Pattern("(?:^\\d+$)|(?:^\\d+\\.\\.$)|(?:^\\.\\.\\d+$)|(?:^\\d+\\.\\.\\d+$)") String input) throws IllegalArgumentException {
        if (input.matches("^\\d+$")) {
            final int value = Integer.parseInt(input);
            return new LevelRange(value, value);
        }
        else if (input.matches("^\\d+\\.\\.$")) {
            final int min = Integer.parseInt(input.substring(0, input.length() - 2));
            return new LevelRange(min, null);
        }
        else if (input.matches("^\\.\\.\\d+$")) {
            final int max = Integer.parseInt(input.substring(2));
            return new LevelRange(null, max);
        }
        else if (input.matches("^\\d+\\.\\.\\d+$")) {
            final String[] separated = input.split("\\.\\.");
            if (separated.length > 2) throw new IllegalArgumentException("NEVER HAPPENS");
            final int min = Integer.parseInt(separated[0]);
            final int max = Integer.parseInt(separated[1]);
            return new LevelRange(min, max);
        }
        else throw new IllegalArgumentException("無効な文字列です");
    }
}
