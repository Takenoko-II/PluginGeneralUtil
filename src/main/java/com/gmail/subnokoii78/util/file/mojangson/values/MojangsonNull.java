package com.gmail.subnokoii78.util.file.mojangson.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MojangsonNull extends MojangsonPrimitive<Object> {
    private MojangsonNull() {
        super(null);
    }

    @Override
    public @Nullable Object getValue() {
        return super.getValue();
    }

    @Override
    public @NotNull String toString() {
        return "null";
    }

    public static final MojangsonNull NULL = new MojangsonNull();
}
