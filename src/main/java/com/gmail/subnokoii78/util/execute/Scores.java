package com.gmail.subnokoii78.util.execute;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Scores extends HashMap<String, NumberRange.ScoreRange> {
    private Scores() {
        super();
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        @RegExp
        private static final String PATTERN = "^[\\s\\n]*[^\\s\\n=]+[\\s\\n]*=[\\s\\n]*(?:\\d+|\\d\\.\\.|\\.\\.\\d|\\d\\.\\.\\d)[\\s\\n]*$";

        private final Scores scores = new Scores();

        private Builder() {}

        public @NotNull Builder and(@NotNull @Pattern(value = PATTERN) String value) {
            final String[] separated = value.split("=");

            if (separated.length > 2) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final String objective = separated[0].trim();

            if (objective.contains(" ") || objective.contains("\n")) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final String range = separated[1].replaceAll("[\\s\\n]+", "").trim();

            scores.put(objective, NumberRange.score(range));

            return this;
        }

        public @NotNull Scores build() {
            return scores;
        }
    }
}
