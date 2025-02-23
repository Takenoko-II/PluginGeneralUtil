package com.gmail.subnokoii78.util.file.json.values;

import org.jetbrains.annotations.NotNull;

public final class JSONNumber extends JSONPrimitive<Number> {
    private JSONNumber(@NotNull Number value) {
        super(value);
    }

    public byte byteValue() {
        return value.byteValue();
    }

    public short shortValue() {
        return value.shortValue();
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public static @NotNull JSONNumber valueOf(@NotNull Number value) {
        return new JSONNumber(value);
    }
}
