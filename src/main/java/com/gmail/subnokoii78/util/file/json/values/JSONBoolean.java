package com.gmail.subnokoii78.util.file.json.values;

import org.jetbrains.annotations.NotNull;

public final class JSONBoolean extends JSONPrimitive<Boolean> {
    private JSONBoolean(boolean value) {
        super(value);
    }

    public static @NotNull JSONBoolean valueOf(boolean value) {
        return new JSONBoolean(value);
    }
}
