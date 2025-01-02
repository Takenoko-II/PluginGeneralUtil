package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public final class MojangsonInt extends MojangsonNumber<Integer> {
    private MojangsonInt(int value) {
        super(value);
    }

    public static @NotNull MojangsonInt valueOf(int value) {
        return new MojangsonInt(value);
    }
}
