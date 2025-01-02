package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public final class MojangsonDouble extends MojangsonNumber<Double> {
    private MojangsonDouble(double value) {
        super(value);
    }

    public static @NotNull MojangsonDouble valueOf(double value) {
        return new MojangsonDouble(value);
    }
}
