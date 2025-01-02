package com.gmail.subnokoii78.util.file.mojangson.values;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonValue;
import org.jetbrains.annotations.NotNull;

public class MojangsonString extends MojangsonValue<String> {
    private MojangsonString(@NotNull String value) {
        super(value);
    }

    public static @NotNull MojangsonString valueOf(@NotNull String value) {
        return new MojangsonString(value);
    }
}
