package com.gmail.subnokoii78.util.file.json.values;

import org.jetbrains.annotations.NotNull;

public final class JSONString extends JSONPrimitive<String> {
    private JSONString(@NotNull String value) {
        super(value);
    }

    public static @NotNull JSONString valueOf(@NotNull String value) {
        return new JSONString(value);
    }
}
