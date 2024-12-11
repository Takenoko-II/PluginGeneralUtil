package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Scores extends HashMap<String, NumberRange.ScoreRange> {
    private Scores() {
        super();
    }

    public static @NotNull Scores of(@NotNull String... values) {
        final Scores scores = new Scores();

        for (final String value : values) {
            final String[] separated = value.split("=");

            if (separated.length > 2) {
                throw new IllegalArgumentException("無効な形式です");
            }

            final String objective = separated[0].trim();
            final String range = separated[1].replaceAll("[\\s\\n]+", "").trim();

            scores.put(objective, NumberRange.score(range));
        }

        return scores;
    }
}
