package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public final class MojangsonFloat extends MojangsonNumber<Float> {
    private MojangsonFloat(float value) {
        super(value);
    }

    public static @NotNull MojangsonFloat valueOf(float value) {
        return new MojangsonFloat(value);
    }
}
