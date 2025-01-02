package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public final class MojangsonLong extends MojangsonNumber<Long> {
    private MojangsonLong(long value) {
        super(value);
    }

    public static @NotNull MojangsonLong valueOf(long value) {
        return new MojangsonLong(value);
    }
}
