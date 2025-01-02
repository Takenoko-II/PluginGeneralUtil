package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;

public class MojangsonString extends MojangsonPrimitive<String> {
    private MojangsonString(@NotNull String value) {
        super(value);
    }

    public static @NotNull MojangsonString valueOf(@NotNull String value) {
        return new MojangsonString(value);
    }
}
